package cn.rongcapital.chorus.server.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by abiton on 8/23/16.
 */
@EnableCaching
@Configuration
@ConditionalOnProperty(name = "spring.redis.environment", havingValue = "cluster")
public class RedisClusterConfig {

    @Bean
    public ClusterConfigurationProperties clusterConfigurationProperties(){
        return new ClusterConfigurationProperties();
    }

    @Bean
    public RedisClusterConfiguration getClusterConfig(ClusterConfigurationProperties clusterProperties) {
        return new RedisClusterConfiguration(clusterProperties.getNodes());
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory(RedisClusterConfiguration redisClusterConfiguration) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setTestWhileIdle(true);
        poolConfig.setMaxIdle(3);
        JedisConnectionFactory factory = new JedisConnectionFactory(redisClusterConfiguration);
        factory.setUsePool(true);
        factory.setPoolConfig(poolConfig);
        return factory;
    }

    @Bean
    public RedisTemplate redisTemplate(JedisConnectionFactory jedisConnectionFactory) {
        RedisTemplate template = new RedisTemplate();
        template.setConnectionFactory(jedisConnectionFactory);
        return template;
    }

}
