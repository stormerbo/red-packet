package cn.ddlover.redpacket.entity.req;

import lombok.Data;

/**
 * @author stormer.xia
 * @version 1.0
 * @date 2021/4/15 16:36
 */
@Data
public class RobRedPacketReq {

  /**
   * 当前用户id
   */
  private Integer userId;

  /**
   * 红包id
   */
  private Integer redPacketId;
}
