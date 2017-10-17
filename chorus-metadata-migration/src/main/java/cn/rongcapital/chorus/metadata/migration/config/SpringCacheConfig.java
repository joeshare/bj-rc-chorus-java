package cn.rongcapital.chorus.metadata.migration.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static cn.rongcapital.chorus.common.constant.XdConstants.XD_APPLICATION_ID_CACHE_EXPIRE_RATE;
import static cn.rongcapital.chorus.common.constant.XdConstants.XD_APPLICATION_ID_CACHE_KEY;

/**
 * Created by alan on 11/25/16.
 */
@Configuration
public class SpringCacheConfig {


    @Bean
    @ConfigurationProperties(prefix = "spring.cache.redis")
    public CacheManager cacheManager(RedisTemplate redisTemplate) {
        RedisCacheManager redisCacheManager = new RedisCacheManager(redisTemplate);
        Map<String, Long> expireMap = new ConcurrentHashMap<>();
        expireMap.put(XD_APPLICATION_ID_CACHE_KEY, XD_APPLICATION_ID_CACHE_EXPIRE_RATE);
        redisCacheManager.setExpires(expireMap);
        return redisCacheManager;
    }

}
