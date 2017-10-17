package cn.rongcapital.chorus.hive.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by hhlfl on 2017-9-6.
 */
public class SimpleHiveDataSource extends AbstractDataSource{
    private String url;
    private Properties sessionProperties;
    private String username;
    private String password;
    private Properties connectionProperties;

    public SimpleHiveDataSource() {
    }

    public SimpleHiveDataSource(String driverClassName, String url) {
        setDriverClassName(driverClassName);
        setUrl(url);
    }

    public SimpleHiveDataSource(String driverClassName, String url, String username, String password) {
        setDriverClassName(driverClassName);
        setUrl(url);
        setUsername(username);
        setPassword(password);
    }

    public SimpleHiveDataSource(String driverClassName, String url, Properties conProps) {
        setDriverClassName(driverClassName);
        setUrl(url);
        setConnectionProperties(conProps);
    }

    public SimpleHiveDataSource(String driverClassName, String url, Properties conProps, Properties sessionProperties) {
        setDriverClassName(driverClassName);
        setUrl(url);
        setConnectionProperties(conProps);
        setSessionProperties(sessionProperties);
    }


    public void setSessionProperties(Properties sessionProperties) {
        this.sessionProperties = sessionProperties;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setConnectionProperties(Properties connectionProperties) {
        this.connectionProperties = connectionProperties;
    }

    public void setUrl(String url){
        if(url==null || "".equals(url.trim())) throw new IllegalArgumentException("url must be not null.");
        this.url=url.trim();
    }

    public void setDriverClassName(String driverClassName) {
        if(driverClassName == null || "".equals(driverClassName.trim())) throw new IllegalArgumentException("Property 'driverClassName' must not be empty");
        String driverClassNameToUse = driverClassName.trim();
        try {
            Class.forName(driverClassNameToUse);
        } catch (ClassNotFoundException var4) {
            throw new IllegalStateException("Could not load JDBC driver class [" + driverClassNameToUse + "]", var4);
        }

        if(this.logger.isInfoEnabled()) {
            this.logger.info("Loaded JDBC driver: " + driverClassNameToUse);
        }
    }

    public Connection getConnection() throws SQLException {
        return this.getConnectionFromDriver(this.username, this.password);
    }

    public Connection getConnection(String username, String password) throws SQLException {
        return this.getConnectionFromDriver(username, password);
    }

    protected Connection getConnectionFromDriver(String username, String password) throws SQLException {
        Properties mergedProps = new Properties();
        Properties connProps = this.connectionProperties;
        if(connProps != null) {
            mergedProps.putAll(connProps);
        }

        if(username != null) {
            mergedProps.setProperty("user", username);
        }

        if(password != null) {
            mergedProps.setProperty("password", password);
        }

        return this.getConnectionFromDriver(mergedProps);
    }

    protected Connection getConnectionFromDriver(Properties props) throws SQLException {
        String url = buildUrl(this.url,sessionProperties);
        if(this.logger.isDebugEnabled()) {
            this.logger.debug("Creating new JDBC Driver Connection to [" + url + "]");
        }

        return DriverManager.getConnection(url, props);
    }

    /***
     * url like:jdbc:hive2://<zookeeper quorum>/;serviceDiscoveryMode=zooKeeper;zooKeeperNamespace=hiveserver2?tez.queue.name=hive1&hive.server2.thrift.resultset.serialize.in.tasks=true
     *
     * @param url
     * @param sessionProperties
     * @return
     */
    public String buildUrl(String url, Properties sessionProperties){
        if(sessionProperties == null)
            return url;


        String connectParamStr ="";
        String db ="";
        String sessionParamStr="";
        String urlP = url;
        int index = urlP.indexOf("?");
        if(index != -1){
            connectParamStr = urlP.substring(index);
            urlP = urlP.substring(0,index);
        }

        index = urlP.indexOf(";");
        if(index != -1){
            sessionParamStr=urlP.substring(index);
            urlP = urlP.substring(0,index);
        }

        index = urlP.lastIndexOf("://");
        if(index != -1) {
            index = urlP.indexOf("/",index+3);
        }else{
            index = urlP.indexOf("/");
        }

        if(index != -1){
            db = urlP.substring(index);
            urlP = urlP.substring(0,index);
        }

        StringBuilder sb = new StringBuilder("");
        for(Object k : sessionProperties.keySet()){
            String key = k.toString();
            String val = sessionProperties.getProperty(key);
            if(Params.PARAM_DATABASE.equals(key)){
                if(val != null && !"".equals(val)){
                    if(db.startsWith("/"))
                        db = db.substring(0,1)+val;
                    else
                        db = "/"+val;
                }
                continue;
            }

            if(val == null) {
                logger.warn(String.format("properties[%s] is null.",key));
                sb.append(";").append(key).append("=");
            }else {
                sb.append(";").append(key).append("=").append(val);
            }
        }

        StringBuilder usb = new StringBuilder();
        usb.append(urlP).append(db).append(sessionParamStr);
        if(sessionParamStr.endsWith(";") && sb.length()>0) {
            usb.append(sb.substring(1));
            usb.append(connectParamStr);
            return usb.toString();
        }

        return usb.append(sb.toString()).append(connectParamStr).toString();
    }
}
