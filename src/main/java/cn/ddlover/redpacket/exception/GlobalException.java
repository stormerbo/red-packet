package cn.ddlover.redpacket.exception;

import cn.ddlover.redpacket.entity.Response;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author stormer.xia
 * @version 1.0
 * @date 2021/4/19 15:25
 */
@RestControllerAdvice
public class GlobalException {

  @ExceptionHandler(Throwable.class)
  public Response<Void> exceptionHandler(Throwable throwable) {
    return Response.fail(throwable.getMessage());
  }
}
