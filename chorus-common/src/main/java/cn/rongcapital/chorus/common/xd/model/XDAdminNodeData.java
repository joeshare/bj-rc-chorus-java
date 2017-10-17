package cn.rongcapital.chorus.common.xd.model;

/**
 * SpringXD Zookeeper节点信息对应模型
 *
 * @author li.hzh
 * @date 2016-11-14 16:19
 */
public class XDAdminNodeData {

    private String ip;
    private String host;
    private int port;
    private String pid;
    private String id;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String adminAddress() {
        return "http://" + getIp() + ":" + getPort();
    }

    public boolean valid() {
        return !(getIp() == null ||
                getPort() == 0);
    }
}
