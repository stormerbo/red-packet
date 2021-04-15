package cn.ddlover.redpacket.entity.resp;

import cn.ddlover.redpacket.entity.RedPacket;
import java.util.List;
import lombok.Data;

/**
 * @author stormer.xia
 * @version 1.0
 * @date 2021/4/15 19:00
 */
@Data
public class RedPacketVO extends RedPacket{

  /**
   * 红包被抢的记录
   */
  private List<RedPacketRecordVO> redPacketRecordList;
}
