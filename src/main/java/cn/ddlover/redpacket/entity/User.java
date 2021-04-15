package cn.ddlover.redpacket.entity;

import java.math.BigDecimal;
import lombok.Data;

/**
 * @author stormer.xia
 * @version 1.0
 * @date 2021/4/15 14:24
 */
@Data
public class User {

  private Integer userId;

  private String userName;

  private BigDecimal balance;
}
