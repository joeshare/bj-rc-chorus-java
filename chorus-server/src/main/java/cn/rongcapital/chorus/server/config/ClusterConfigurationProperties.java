package cn.rongcapital.chorus.server.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Created by abiton on 8/29/16.
 */
@ConfigurationProperties(prefix = "spring.redis.cluster")
public class ClusterConfigurationProperties {
    @Setter
    @Getter
    List<String> nodes;
}
