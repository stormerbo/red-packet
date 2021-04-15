package cn.ddlover.redpacket.controller;

import cn.ddlover.redpacket.entity.Response;
import cn.ddlover.redpacket.entity.req.CreateRedPacketReq;
import cn.ddlover.redpacket.entity.req.QueryRedPacketReq;
import cn.ddlover.redpacket.entity.req.RobRedPacketReq;
import cn.ddlover.redpacket.entity.resp.RedPacketVO;
import cn.ddlover.redpacket.service.RedPacketService;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author stormer.xia
 * @version 1.0
 * @date 2021/4/15 14:33
 * TODO: 限流，建议在网关层自己设计
 */
@RestController
public class RedPacketController {

  @Autowired
  private RedPacketService redPacketService;

  /**
   * 发红包
   * 这里不需要返回红包的信息，一般由消息系统发送这个红包
   */
  @PostMapping("/create-red-packet")
  public Response<Void> createRedPacket(@RequestBody CreateRedPacketReq createRedPacketReq) {
    redPacketService.createRedPacket(createRedPacketReq);
    return Response.success();
  }

  @PostMapping("/rob-red-packet")
  public Response<BigDecimal> robRedPacket(@RequestBody RobRedPacketReq robRedPacketReq) {
    BigDecimal bigDecimal = redPacketService.robRedPacket(robRedPacketReq);
    return Response.successWithData(bigDecimal);
  }

  @PostMapping("/query-red-packet")
  public Response<RedPacketVO> queryRedPacket(@RequestBody QueryRedPacketReq queryRedPacketReq) {
    return Response.successWithData(redPacketService.queryRedPacket(queryRedPacketReq));
  }
}
