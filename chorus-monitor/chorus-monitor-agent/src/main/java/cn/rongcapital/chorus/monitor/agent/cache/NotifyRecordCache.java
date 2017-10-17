package cn.rongcapital.chorus.monitor.agent.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache the notify record with timestamp.
 * Using for avoid continuously sending message of the same event.
 */
public class NotifyRecordCache {

    private static final Map<String, CacheRecord> cache = new ConcurrentHashMap<>();

    public static void cache(String key, CacheRecord value) {
        cache.put(key, value);
    }

    public static CacheRecord getRecord(String key) {
        return cache.get(key);
    }

}
