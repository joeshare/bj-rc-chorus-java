package cn.rongcapital.chorus.server.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;

/**
 * 数据源配置
 *
 * @author li.hzh
 * @date 2016-02-29
 */
@Configuration
public class DatasourceConfig {

//    @Bean(name = "hiveDataSource")
//    @ConfigurationProperties(prefix = "hive.jdbc")
//    public SimpleDriverDataSource druidHiveDataSource() {
//        return new SimpleDriverDataSource();
//    }

}
