package cn.rongcapital.chorus.server.config;

import cn.rongcapital.chorus.das.service.impl.TableInfoServiceV2Impl;
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
import static cn.rongcapital.chorus.das.service.impl.TableInfoServiceV2Impl.EXPIRE_TIME;
import static cn.rongcapital.chorus.das.service.impl.TableInfoServiceV2Impl.TABLE_CACHE_NAME;

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
        expireMap.put(TABLE_CACHE_NAME, EXPIRE_TIME);
        redisCacheManager.setExpires(expireMap);
        return redisCacheManager;
    }
}
