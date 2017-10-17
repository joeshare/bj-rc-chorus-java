package cn.rongcapital.chorus.modules.solr.health.indicator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author yimin
 */
@Slf4j
@Component
public class LeaderIndicator implements Indicator {
    @Override
    public Indications indicate(ClusterState clusterState) {
        {
            final Indications indications = new Indications();
            Map<String, Shards> cluster = clusterState.getCluster();
            cluster.values().forEach(shards -> shards.shards.forEach(shard -> {
                final String solrCollectionName = shards.getSolrCollectionName();
                final String shardName = shard.getName();
                log.info("SOLR COLLECTION {} SHARD {} LEADER:", solrCollectionName, shardName);
                long actual = shard.replicas.stream().filter(replica -> {
                    log.info("----{}\tleader {}", replica.getName(), replica.isLeader());
                    return replica.isLeader();
                }).count();
                if (actual < 1) {
                    indications.indication(Indication.builder()
                                                     .actual(actual)
                                                     .expect(1)
                                                     .message(String.format("there is not any leader for %s.%s", solrCollectionName, shardName))
                                                     .status(false)
                                                     .build());
                }
            }));
            return indications;
        }
    }
}
