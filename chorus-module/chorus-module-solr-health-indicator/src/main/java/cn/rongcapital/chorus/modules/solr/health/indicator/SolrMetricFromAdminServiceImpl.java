package cn.rongcapital.chorus.modules.solr.health.indicator;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.fluent.Request;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author yimin
 */
@Slf4j
@Service
public class SolrMetricFromAdminServiceImpl implements SolrMetricService {
    public static final String CLUSTER_STATE_URI = "http://10.200.32.15:8886/solr/admin/zookeeper?detail=true&path=%2Fclusterstate.json&view=graph&wt=json";
    public static final String LIVE_NODES_URI    = "http://10.200.32.15:8886/solr/admin/zookeeper?path=%2Flive_nodes&wt=json";

    @Override
    public String liveNodes() {
        try {
            return Request.Get(LIVE_NODES_URI).connectTimeout(1000).socketTimeout(1000).execute().returnContent().asString();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public ClusterState clusterState() {
        try {
            return new ClusterState(Request.Get(CLUSTER_STATE_URI).connectTimeout(1000).socketTimeout(1000).execute().returnContent().asString());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }
}
