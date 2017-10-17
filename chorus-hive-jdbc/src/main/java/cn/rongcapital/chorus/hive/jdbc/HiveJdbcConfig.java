package cn.rongcapital.chorus.hive.jdbc;

import java.util.Properties;

/**
 * Created by hhlfl on 2017-9-7.
 */
public class HiveJdbcConfig {
    private String driverClassName="org.apache.hive.jdbc.HiveDriver";
    private String url;
    private String user="";
    private String password="";
    private Properties sessionProp;
    private Properties connectProp;
    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Properties getSessionProp() {
        return sessionProp;
    }

    public void setSessionProp(Properties sessionProp) {
        this.sessionProp = sessionProp;
    }

    public Properties getConnectProp() {
        return connectProp;
    }

    public void setConnectProp(Properties connectProp) {
        this.connectProp = connectProp;
    }
}
