package cn.ddlover.redpacket.exception;

/**
 * @author stormer.xia
 * @version 1.0
 * @date 2021/4/15 16:38
 */
public class RobRedPacketFailedException extends RuntimeException{

  public RobRedPacketFailedException() {
    super();
  }

  public RobRedPacketFailedException(String message) {
    super(message);
  }
}
