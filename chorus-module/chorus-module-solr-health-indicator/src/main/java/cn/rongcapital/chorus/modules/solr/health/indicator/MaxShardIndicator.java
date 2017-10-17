package cn.rongcapital.chorus.modules.solr.health.indicator;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author yimin
 */
@Slf4j
@Component
public class MaxShardIndicator implements Indicator {
    @Override
    public Indications indicate(ClusterState clusterState) {
        final Indications indications = new Indications();
        Map<String, Shards> cluster = clusterState.getCluster();
        cluster.forEach((collection, shards) -> {
            int maxShardsPerNode = shards.maxShardsPerNode;
            log.info("SOLR COLLECTION {} SHARDS:", shards.getSolrCollectionName());
            long actual = shards.shards.stream()
                                       .filter(shard -> {
                                           log.info("----{}\t{}", shard.getName(), shard.getState());
                                           return StringUtils.equals("active", shard.getState());
                                       }).count();
            if (actual != maxShardsPerNode) {
                indications.indication(Indication.builder()
                                                 .actual(actual)
                                                 .expect(maxShardsPerNode)
                                                 .message(String.format("the actual maxShardsPerNode %s not match the config %s in %s", actual, maxShardsPerNode, collection))
                                                 .status(false)
                                                 .build());
            }
        });
        return indications;
    }
}
