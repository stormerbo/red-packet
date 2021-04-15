package cn.ddlover.redpacket.service;

import cn.ddlover.redpacket.entity.RedPacket;
import cn.ddlover.redpacket.entity.RedPacketRecord;
import cn.ddlover.redpacket.entity.User;
import cn.ddlover.redpacket.entity.req.CreateRedPacketReq;
import cn.ddlover.redpacket.entity.req.QueryRedPacketReq;
import cn.ddlover.redpacket.entity.req.RobRedPacketReq;
import cn.ddlover.redpacket.entity.resp.RedPacketRecordVO;
import cn.ddlover.redpacket.entity.resp.RedPacketVO;
import cn.ddlover.redpacket.exception.BalanceNotEnoughException;
import cn.ddlover.redpacket.exception.RobRedPacketFailedException;
import cn.ddlover.redpacket.mapper.RedPacketMapper;
import cn.ddlover.redpacket.mapper.UserMapper;
import cn.ddlover.redpacket.util.SnowFlake;
import com.alibaba.fastjson.JSON;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import jdk.internal.org.objectweb.asm.tree.FieldInsnNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * @author stormer.xia
 * @version 1.0
 * @date 2021/4/15 14:39
 */
@Slf4j
@Service
public class RedPacketService {

  @Autowired
  private UserMapper userMapper;

  @Autowired
  private RedPacketMapper redPacketMapper;
  @Autowired
  private RedisTemplate<String, Object> redisTemplate;
  @Autowired
  private KafkaTemplate<String, Object> kafkaTemplate;
  @Autowired
  private SnowFlake snowFlake;

  private final static Map<Integer, RedPacketVO> LOCAL_CACHE = new ConcurrentHashMap<>();

  private static final double MIN_RED_PACKET_MONEY = 0.01;

  @Transactional(rollbackFor = Exception.class)
  public void createRedPacket(CreateRedPacketReq createRedPacketReq) {
    User user = userMapper.queryByUserId(createRedPacketReq.getUserId());
    BigDecimal balance = user.getBalance().add(createRedPacketReq.getTotalMoney().negate());
    if (balance.compareTo(BigDecimal.ZERO) < 0) {
      throw new BalanceNotEnoughException();
    }
    user.setBalance(balance);
    // 更新账户余额，插入红包记录
    userMapper.updateUser(user);
    RedPacket redPacket = buildRedPacket(user, createRedPacketReq);
    redPacketMapper.insert(redPacket);
    // 生成完整的红包数据
    List<BigDecimal> redPacketList = generateRedPacketList(createRedPacketReq);
    // 存储红包数据
    redisTemplate.opsForValue().set("red-packet:info:" + redPacket.getRedPacketId(), redPacket);
    // 利用redis存储红包数据
    redPacketList.forEach(money -> redisTemplate.opsForList().leftPushAll("red-packet:" + redPacket.getRedPacketId(), money));
  }

  /**
   * 抢红包
   */
  public BigDecimal robRedPacket(RobRedPacketReq robRedPacketReq) {
    // 判断用户是否已经抢过
    Long count = redisTemplate.opsForSet().add("robbed-red-packet:" + robRedPacketReq.getRedPacketId(), robRedPacketReq.getUserId());
    if (Objects.isNull(count) || count <= 0) {
      throw new RobRedPacketFailedException("用户已经抢过该红包");
    }
    // 如果redis挂了，这里抢红包就直接失败了
    BigDecimal money = (BigDecimal) redisTemplate.opsForList().leftPop("red-packet:" + robRedPacketReq.getRedPacketId());
    if (Objects.isNull(money)) {
      throw new RobRedPacketFailedException("红包已经被抢光了");
    }
    RedPacketRecord redPacketRecord = new RedPacketRecord();
    redPacketRecord.setRedPacketId(robRedPacketReq.getRedPacketId());
    redPacketRecord.setUserId(robRedPacketReq.getUserId());
    redPacketRecord.setMoney(money);
    redPacketRecord.setSerialNo(snowFlake.nextId());

    // 发送给kafka， 异步入库，
    kafkaTemplate.send("red-packet-record", redPacketRecord.getRedPacketId().toString(), JSON.toJSONString(redPacketRecord)).addCallback(success -> {
      log.info("发送数据到kafka成功");
    }, fail -> {
      // 失败重试
      kafkaTemplate.send("red-packet-record", redPacketRecord.getRedPacketId().toString(), JSON.toJSONString(redPacketRecord));
    });

    return money;
  }

  public RedPacketVO queryRedPacket(QueryRedPacketReq queryRedPacketReq) {
    if (LOCAL_CACHE.containsKey(queryRedPacketReq.getRedPacketId())) {
      return LOCAL_CACHE.get(queryRedPacketReq.getRedPacketId());
    }
    RedPacket redPacket = (RedPacket) redisTemplate.opsForValue().get("red-packet:info:" + queryRedPacketReq.getRedPacketId());
    if (Objects.isNull(redPacket)) {
      redPacket = redPacketMapper.queryById(queryRedPacketReq.getRedPacketId());
      if (Objects.isNull(redPacket)) {
        throw new RedPacketNotExitsException();
      }
      redisTemplate.opsForValue().set("red-packet:info:" + redPacket.getRedPacketId(), redPacket);
    }
    RedPacketVO redPacketVO = new RedPacketVO();
    BeanUtils.copyProperties(redPacket, redPacketVO);
    List<Object> list = redisTemplate.opsForList().range("red-packet-record:" + queryRedPacketReq.getRedPacketId(), 0, -1);
    if (!CollectionUtils.isEmpty(list)) {
      List<RedPacketRecordVO> recordList = list.stream().map(obj -> (RedPacketRecordVO) obj).collect(Collectors.toList());
      redPacketVO.setRedPacketRecordList(recordList);
    }
    // 红包抢完了，状态不会有变化了，直接打入本地缓存
    if (redPacketVO.getStatus() == 1) {
      // 记录本地缓存
      LOCAL_CACHE.put(queryRedPacketReq.getRedPacketId(), redPacketVO);
    }
    return redPacketVO;
  }

  private RedPacket buildRedPacket(User user, CreateRedPacketReq createRedPacketReq) {
    RedPacket redPacket = new RedPacket();
    redPacket.setUserId(user.getUserId());
    redPacket.setNum(createRedPacketReq.getNumber());
    redPacket.setTotalMoney(createRedPacketReq.getTotalMoney());
    redPacket.setRemainMoney(createRedPacketReq.getTotalMoney());
    return redPacket;
  }

  /**
   * 生成可抢的红包列表
   * 可以优化成 策略模式，支持多种红包生成
   */
  private List<BigDecimal> generateRedPacketList(CreateRedPacketReq createRedPacketReq) {
    List<BigDecimal> list = new ArrayList<>();
    BigDecimal totalMoney = createRedPacketReq.getTotalMoney();
    Integer number = createRedPacketReq.getNumber();
    BigDecimal deal = BigDecimal.ZERO;
    // 前面n-1个使用随机生成，最后一个为剩余的钱
    for (int i = 1; i <= number - 1; i++) {
      // 保证剩余的都有至少1分钱
      double maxValue = totalMoney.add(deal.negate()).doubleValue() - (number - i) * MIN_RED_PACKET_MONEY;
      double money = Math.random() * (maxValue - MIN_RED_PACKET_MONEY) + MIN_RED_PACKET_MONEY;
      BigDecimal bigDecimal = new BigDecimal(money).setScale(2, RoundingMode.HALF_UP);
      deal = deal.add(bigDecimal);
      list.add(bigDecimal);
    }
    list.add(totalMoney.add(deal.negate()));
    return list;
  }
}
