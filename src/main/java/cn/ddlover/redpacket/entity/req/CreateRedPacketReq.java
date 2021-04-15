package cn.ddlover.redpacket.entity.req;

import java.math.BigDecimal;
import lombok.Data;

/**
 * @author stormer.xia
 * @version 1.0
 * @date 2021/4/15 14:36
 *
 * 发红包的请求
 */
@Data
public class CreateRedPacketReq {

  /**
   * 用户的id
   * 一般是根据登录态自动选择， 这里方便测试采用传入
   */
  private Integer userId;

  /**
   * 红包的总金额
   */
  private BigDecimal totalMoney;

  /**
   * 红包的个数
   */
  private Integer number;
}
