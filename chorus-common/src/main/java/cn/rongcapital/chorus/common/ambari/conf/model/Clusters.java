package cn.rongcapital.chorus.common.ambari.conf.model;

import java.util.List;

public class Clusters {
    List<QConfig> desired_config;

    public List<QConfig> getDesired_config() {
        return desired_config;
    }

    public void setDesired_config(List<QConfig> desired_config) {
        this.desired_config = desired_config;
    }

}
