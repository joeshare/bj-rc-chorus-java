package cn.rongcapital.chorus.modules.solr.health.indicator;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yimin
 */
public class ClusterState {
    private static final ArrayList<String> focus = new ArrayList<String>() {{
        add("vertex_index");
        add("edge_index");
        add("fulltext_index");
    }};
    private final String              jsonContent;
    @Getter
    private final Map<String, Shards> cluster;

    ClusterState(String jsonContent) {
        this.jsonContent = jsonContent;
        this.cluster = _parse();
    }

    private Map<String, Shards> _parse() {
        JSONObject data = JSON.parseObject(this.jsonContent).getJSONObject("znode").getJSONObject("data");
        Map<String, Shards> collection = new HashMap<>(3);
        focus.forEach(solrCollectionName -> {
            JSONObject collectionObject = data.getJSONObject(solrCollectionName);
            Integer replicationFactor = collectionObject.getInteger("replicationFactor");
            Integer maxShardsPerNode = collectionObject.getInteger("maxShardsPerNode");
            if (!collection.containsKey(solrCollectionName)) {
                collection.put(solrCollectionName, new Shards(solrCollectionName, replicationFactor, maxShardsPerNode));
            }
            JSONObject shards = collectionObject.getJSONObject("shards");
            shards.keySet().forEach(shardName -> {
                JSONObject shardObject = shards.getJSONObject(shardName);
                Shard shard = new Shard(shardName, shardObject.getString("state"));
                collection.get(solrCollectionName).add(shard);
                JSONObject replicasObject = shardObject.getJSONObject("replicas");
                replicasObject.keySet().forEach(replicaName -> {
                    JSONObject replicaObject = replicasObject.getJSONObject(replicaName);
                    Replica replica = Replica.builder().name(replicaName)
                                             .core(replicaObject.getString("core"))
                                             .base_url(replicaObject.getString("base_url"))
                                             .node_name(replicaObject.getString("node_name"))
                                             .state(replicaObject.getString("state"))
                                             .leader(replicaObject.getBooleanValue("leader")).build();
                    shard.add(replica);
                });
            });
        });
        return collection;
    }

    @Override
    public String toString() {
        return this.jsonContent;
    }
}
