package cn.ddlover.redpacket.entity;

import com.alibaba.fastjson.annotation.JSONField;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * @author stormer.xia
 * @version 1.0
 * @date 2021/4/15 14:25
 */
@Data
public class RedPacket {

  private Integer redPacketId;

  private Integer userId;

  private BigDecimal totalMoney;

  private Integer num;

  private BigDecimal remainMoney;

  /**
   * 0-未抢完，1-已经抢完
   */
  private Integer status = 0;

  @JSONField(format = "yyyyMMdd HH:mm:ss")
  private Date createTime;
}
