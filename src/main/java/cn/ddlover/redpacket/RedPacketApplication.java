package cn.ddlover.redpacket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author stormer.xia
 */
@EnableTransactionManagement
@SpringBootApplication
public class RedPacketApplication {

  public static void main(String[] args) {
    SpringApplication.run(RedPacketApplication.class, args);
  }

}
