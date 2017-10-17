package cn.rongcapital.chorus.modules.resource.kpi.snapshot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by hhlfl on 2017-7-18.
 */
@Configuration
public class TestConfig {
    protected DataSource ds;
    protected DataSource xdDS;

    public TestConfig() {
        String driveClassName="com.mysql.jdbc.Driver";
        String url="jdbc:mysql://10.200.48.79:3306/chorus?useUnicode=true&characterEncoding=utf-8&useSSL=false";
        String userName="dps";
            String password="Dps@10.200.48.MySQL";
        ds = new DataSource();
        ds.setDriverClassName(driveClassName);
        ds.setUrl(url);
        ds.setUserName(userName);
        ds.setPassword(password);

        url="jdbc:mysql://10.200.48.79:3306/xd?useUnicode=true&characterEncoding=utf-8&useSSL=false";
        xdDS = new DataSource();
        xdDS.setDriverClassName(driveClassName);
        xdDS.setUrl(url);
        xdDS.setUserName(userName);
        xdDS.setPassword(password);
    }

    @Bean(name = "chorusDataSource")
    DataSource getChorusDataSource(){
        return this.ds;
    }

    @Bean(name="xdDataSource")
    DataSource getXdDataSource(){
        return this.xdDS;
    }
}
