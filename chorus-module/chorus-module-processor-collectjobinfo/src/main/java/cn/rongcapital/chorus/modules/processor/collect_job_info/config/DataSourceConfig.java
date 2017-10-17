package cn.rongcapital.chorus.modules.processor.collect_job_info.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;

/**
 * Created by abiton on 10/08/2017.
 */
@Configuration
@PropertySource("config/collect_job_info_processor.properties")
@MapperScan(basePackages={"cn.rongcapital.chorus.modules.processor.collect_job_info.dao"},sqlSessionFactoryRef = DataSourceConfig.SQL_SESSION_FACTORY_NAME)
public class DataSourceConfig {

    @Value("${datasource.chorus.url}")
    private String url;

    @Value("${datasource.chorus.username}")
    private String userName;
    @Value("${datasource.chorus.password}")
    private String password;
    public static final String SQL_SESSION_FACTORY_NAME = "sessionFactoryChorus";
    @Bean(name = "datasourceChorus")
    @Primary
    @ConfigurationProperties(prefix = "datasource.chorus")
    public DataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(url);
        dataSource.setPassword(password);
        dataSource.setUsername(userName);
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        return dataSource;
    }
    @Bean(name = SQL_SESSION_FACTORY_NAME)
    @Primary
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource());
        return sessionFactoryBean.getObject();
    }
}
