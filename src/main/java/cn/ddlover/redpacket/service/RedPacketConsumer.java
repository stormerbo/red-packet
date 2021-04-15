package cn.ddlover.redpacket.service;

import cn.ddlover.redpacket.entity.RedPacket;
import cn.ddlover.redpacket.entity.RedPacketRecord;
import cn.ddlover.redpacket.entity.User;
import cn.ddlover.redpacket.entity.resp.RedPacketRecordVO;
import cn.ddlover.redpacket.mapper.RedPacketMapper;
import cn.ddlover.redpacket.mapper.RedPacketRecordMapper;
import cn.ddlover.redpacket.mapper.UserMapper;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import java.math.BigDecimal;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author stormer.xia
 * @version 1.0
 * @date 2021/4/15 16:47
 */
@Service
public class RedPacketConsumer {

  @Autowired
  private UserMapper userMapper;
  @Autowired
  private RedPacketMapper redPacketMapper;
  @Autowired
  private RedPacketRecordMapper redPacketRecordMapper;
  @Autowired
  private RedisTemplate<String, Object> redisTemplate;

  @KafkaListener(topics = {"red-packet-record"})
  @Transactional(rollbackFor = Exception.class)
  public void recordRedPacket(String record) {
    RedPacketRecord redPacketRecord = JSON.parseObject(record, new TypeReference<RedPacketRecord>() {
    });
    // 需要判断重复消费
    Long serialNo = redPacketRecord.getSerialNo();
    if (redPacketRecordMapper.countBySerialNo(serialNo) > 0) {
      // 说明之前已经处理过了
      return;
    }
    // 增加账户余额
    User user = userMapper.queryByUserId(redPacketRecord.getUserId());
    BigDecimal balance = user.getBalance().add(redPacketRecord.getMoney());
    user.setBalance(balance);
    userMapper.updateUser(user);
    // 修改红包记录的状态
    RedPacket redPacket = redPacketMapper.queryById(redPacketRecord.getRedPacketId());
    BigDecimal remainMoney = redPacket.getRemainMoney().add(redPacketRecord.getMoney().negate());
    redPacket.setRemainMoney(remainMoney);
    if (remainMoney.compareTo(BigDecimal.ZERO) <= 0) {
      redPacket.setStatus(1);
    }
    // 修改红包的数据
    redPacketMapper.updateById(redPacket);
    // 抢红包记录
    redPacketRecordMapper.insert(redPacketRecord);
    RedPacketRecordVO redPacketRecordVO = new RedPacketRecordVO();
    BeanUtils.copyProperties(redPacketRecord, redPacketRecordVO);
    redPacketRecordVO.setUserName(user.getUserName());
    // 用列表存储抢到红包的用户的id
    redisTemplate.opsForList().leftPush("red-packet-record:" + redPacketRecord.getRedPacketId(), redPacketRecordVO);
  }
}
