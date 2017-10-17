package cn.rongcapital.chorus.hive.jdbc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by hhlfl on 2017-9-7.
 */
public class DataSourceFactory{
    protected final Log log = LogFactory.getLog(DataSourceFactory.class);
    private final static String HIVE_CONFIG_FILE="/hive.properties";
    private final HiveJdbcConfig config = new HiveJdbcConfig();
    private volatile static DataSourceFactory instance ;

    private DataSourceFactory(){
        loadProperties();
    }

    private void loadProperties(){
        InputStream asStream = null;
        Properties properties = new Properties();
        try {
            asStream = DataSourceFactory.class.getResourceAsStream(HIVE_CONFIG_FILE);
            properties.load(asStream);
            //
            if(properties.containsKey("hive.driver"))
                config.setDriverClassName(properties.getProperty("hive.driver"));

            if(properties.containsKey("hive.url")) {
                config.setUrl(properties.getProperty("hive.url"));
            }

            if(properties.containsKey("hive.user"))
                config.setUser("hive.user");

            if(properties.containsKey("hive.password"))
                config.setPassword("hive.password");

        } catch (IOException e) {
            log.error("load hive config properties error.");
            throw new RuntimeException(e.getMessage());
        }finally {
            if(asStream != null)
                try {asStream.close();} catch (IOException e) {}
        }
    }

    public static DataSourceFactory getInstance(){
        if(instance == null){
            synchronized (DataSourceFactory.class) {
                if(instance == null) {
                    instance = new DataSourceFactory();
                }
            }
        }

        return instance;
    }

    public SimpleHiveDataSource createDataSource(){
        return new SimpleHiveDataSource(config.getDriverClassName(),config.getUrl(),config.getUser(),config.getPassword());
    }

    public SimpleHiveDataSource createDataSource(String userName, String password){
        return new SimpleHiveDataSource(config.getDriverClassName(),config.getUrl(),userName,password);
    }

    public SimpleHiveDataSource createDataSource(Properties sessionProperties, Properties connectionProperties){
        Properties connProp = new Properties();
        if(connectionProperties != null) connProp.putAll(connectionProperties);
        Properties sessionProp = new Properties();
        if(sessionProperties != null) sessionProp.putAll(sessionProperties);
        return new SimpleHiveDataSource(config.getDriverClassName(), config.getUrl(),connProp,sessionProp);
    }
}
