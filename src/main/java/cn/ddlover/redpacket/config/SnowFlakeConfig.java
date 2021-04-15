package cn.ddlover.redpacket.config;

import cn.ddlover.redpacket.util.SnowFlake;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author stormer.xia
 * @version 1.0
 * @date 2021/4/15 20:49
 */
@Configuration
public class SnowFlakeConfig {

  @Bean
  public SnowFlake snowFlake() {
    return new SnowFlake(1, 1);
  }
}
