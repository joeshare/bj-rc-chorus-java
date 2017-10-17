package cn.rongcapital.chorus.modules.solr.health.indicator;


import org.junit.Test;

/**
 * @author yimin
 */
public class SolrMetricFromAdminServiceImplTest {
    private SolrMetricService solrMetricService = new SolrMetricFromAdminServiceImpl();

    @Test
    public void liveNodes() throws Exception {
    }

    @Test
    public void clusterState() throws Exception {
        ClusterState clusterState = solrMetricService.clusterState();
    }
}
