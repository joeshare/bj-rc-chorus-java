package cn.rongcapital.chorus.common.ambari.conf.model;

import java.util.Map;

public class QConfig {
    String type;
    String tag;
    Map<String,String> properties;

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getTag() {
        return tag;
    }
    public void setTag(String tag) {
        this.tag = tag;
    }
    public Map<String, String> getProperties() {
        return properties;
    }
    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

}
