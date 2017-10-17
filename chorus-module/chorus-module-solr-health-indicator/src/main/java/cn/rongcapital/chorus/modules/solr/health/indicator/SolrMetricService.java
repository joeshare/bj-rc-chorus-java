package cn.rongcapital.chorus.modules.solr.health.indicator;


/**
 * @author yimin
 */

public interface SolrMetricService {

    //http://10.200.32.15:8886/solr/admin/zookeeper?_=1502443346536&path=%2Flive_nodes&wt=json
//            uri = "/solr/admin/zookeeper?path=%2Flive_nodes&wt=json"
//    @GET("users/{user}/repos")
    String liveNodes();


    //http://10.200.32.15:8886/solr/admin/zookeeper?_=1502443346536&detail=true&path=%2Fclusterstate.json&view=graph&wt=json
//            method = Http.HttpMethod.GET,
//            uri = "/solr/admin/zookeeper"
    ClusterState clusterState();
}
