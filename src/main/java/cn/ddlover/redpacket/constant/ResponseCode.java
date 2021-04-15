package cn.ddlover.redpacket.constant;

import lombok.Getter;

@Getter
public enum ResponseCode {
  /**
   * 成功
   */
  SUCCESS(0, "success"),
  /**
   * 参数异常
   */
  PARAM_INVALID(1111, "param invalid"),

  NOT_LOGIN(401, "Please Login"),

  LOGIN_OTHER(402, "您已在另一台设备登录，本次登录已下线!"),
  /**
   * 失败
   */
  FAIL(9999, "FAIL");

  private Integer code;

  private String message;

  ResponseCode(Integer code, String message) {
    this.code = code;
    this.message = message;
  }
}
