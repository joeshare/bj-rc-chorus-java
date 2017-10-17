package cn.rongcapital.chorus.server.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author yimin
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "spring.redis.single")
public class RedisSingleNodeProperties {
    private Integer dataBase;
    private String  host;
    private Integer port;
    private String  password;
}
