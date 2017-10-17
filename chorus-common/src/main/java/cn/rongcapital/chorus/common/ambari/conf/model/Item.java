package cn.rongcapital.chorus.common.ambari.conf.model;

import java.util.Map;

public class Item {
    Map<String, String> Config;
    String tag;
    String type;
    String version;
    Map<String, String> properties;

    public Map<String, String> getConfig() {
        return Config;
    }
    public void setConfig(Map<String, String> config) {
        Config = config;
    }
    public String getTag() {
        return tag;
    }
    public void setTag(String tag) {
        this.tag = tag;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
    public Map<String, String> getProperties() {
        return properties;
    }
    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

}
