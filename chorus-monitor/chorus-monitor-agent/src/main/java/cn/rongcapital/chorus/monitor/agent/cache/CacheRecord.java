package cn.rongcapital.chorus.monitor.agent.cache;

import lombok.Data;

@Data
public class CacheRecord<T> {

    private T content;
    private long timestamp;

}
