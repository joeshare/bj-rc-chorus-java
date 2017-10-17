package cn.rongcapital.chorus.common.zk;

/**
 * Zookeeper配置类
 *
 * @author li.hzh
 * @date 2016-11-14 10:10
 */
public class ZKConfig {

    private String address;
    private int timeout;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
