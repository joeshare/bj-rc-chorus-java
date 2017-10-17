package cn.rongcapital.chorus.common.ambari.conf.model;

import com.alibaba.fastjson.annotation.JSONField;

public class AmbariQueueConf {
    @JSONField(name="Clusters")
    Clusters clusters;

    public Clusters getClusters() {
        return clusters;
    }

    public void setClusters(Clusters Clustersaaaa) {
        clusters = Clustersaaaa;
    }

}
