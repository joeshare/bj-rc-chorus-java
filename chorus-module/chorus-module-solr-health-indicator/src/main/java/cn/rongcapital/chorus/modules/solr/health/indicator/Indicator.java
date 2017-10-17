package cn.rongcapital.chorus.modules.solr.health.indicator;

/**
 * @author yimin
 */
public interface Indicator {

    Indications indicate(ClusterState clusterState);
}
