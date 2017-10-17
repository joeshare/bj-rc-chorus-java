package cn.rongcapital.chorus.modules.solr.health.indicator;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

/**
 * @author yimin
 */
@Slf4j
//@Component
public class ReplicaIndicator implements Indicator {
    @Override
    public Indications indicate(ClusterState clusterState) {
        final Indications indications = new Indications();
        Map<String, Shards> cluster = clusterState.getCluster();
        cluster.values().forEach(shards -> shards.shards.forEach(shard -> {
            final String solrCollectionName = shards.getSolrCollectionName();
            int replicationFactor = shards.getReplicationFactor();
            final String shardName = shard.getName();
            log.info("SOLR COLLECTION {} SHARD {} REPLICAS:", solrCollectionName, shardName);
            long actual = shard.replicas.stream().filter(replica -> {
                log.info("----{}\t{}", replica.getName(), replica.getState());
                return StringUtils.equals("active", replica.getState());
            }).count();
            if (actual != replicationFactor) {
                indications.indication(Indication.builder()
                                                 .actual(actual)
                                                 .expect(replicationFactor)
                                                 .message(String.format(
                                                         "the actual replicas %s not match the config replicationFactor %s in %s.%s",
                                                         actual,
                                                         replicationFactor,
                                                         solrCollectionName,
                                                         shardName
                                                 ))
                                                 .status(false)
                                                 .build());
            }
        }));
        return indications;
    }
}
