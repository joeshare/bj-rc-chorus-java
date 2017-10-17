package cn.rongcapital.chorus.common.ambari.conf.model;

import java.util.List;
/**
 * Ambari SchedulerCapacity json 对应模型
 * @author Lovett
 */
public class AmbariSchedulerCapacity {
    String href;
    List<Item> items;

    public String getHref() {
        return href;
    }
    public void setHref(String href) {
        this.href = href;
    }
    public List<Item> getItems() {
        return items;
    }
    public void setItems(List<Item> items) {
        this.items = items;
    }

}
