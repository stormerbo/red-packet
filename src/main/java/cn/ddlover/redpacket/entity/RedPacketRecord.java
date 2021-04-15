package cn.ddlover.redpacket.entity;

import com.alibaba.fastjson.annotation.JSONField;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * @author stormer.xia
 * @version 1.0
 * @date 2021/4/15 14:26
 */
@Data
public class RedPacketRecord implements Serializable {

  private Integer redPacketRecordId;

  private Integer redPacketId;

  private Integer userId;

  private Long serialNo;

  private BigDecimal money;

  @JSONField(format = "yyyyMMdd HH:mm:ss")
  private Date createTime;
}
