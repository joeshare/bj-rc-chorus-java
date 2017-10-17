package cn.rongcapital.chorus.modules.solr.health.indicator;

import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yimin
 */
@Getter
public class Shards implements Serializable {
    private static final long serialVersionUID = 7243052240648792614L;

    public final String solrCollectionName;
    public final int    replicationFactor;
    public final int    maxShardsPerNode;
    public final List<Shard> shards = new ArrayList<>(3);

    Shards(String solrCollectionName, int replicationFactor, int maxShardsPerNode) {
        this.solrCollectionName = solrCollectionName;
        this.replicationFactor = replicationFactor;
        this.maxShardsPerNode = maxShardsPerNode;
    }

    void add(Shard one) {
        shards.add(one);
    }
}
