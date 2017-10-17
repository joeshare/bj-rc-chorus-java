package cn.rongcapital.chorus.server.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by abiton on 8/23/16.
 */
@EnableCaching
@Configuration
@ConditionalOnProperty(name = "spring.redis.environment", havingValue = "single", matchIfMissing = true)
public class RedisSingleNodeConfig {

    @Bean
    public RedisSingleNodeProperties redisSingleNodeProperties() {
        return new RedisSingleNodeProperties();
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory(RedisSingleNodeProperties redisSingleNodeProperties) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setTestWhileIdle(true);
        poolConfig.setMaxIdle(3);

        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setDatabase(redisSingleNodeProperties.getDataBase());
        factory.setHostName(redisSingleNodeProperties.getHost());
        factory.setPort(redisSingleNodeProperties.getPort());

        if (StringUtils.isNotBlank(redisSingleNodeProperties.getPassword())) {
            factory.setPassword(redisSingleNodeProperties.getPassword());
        }

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
