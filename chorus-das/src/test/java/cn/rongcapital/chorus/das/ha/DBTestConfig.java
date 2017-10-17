package cn.rongcapital.chorus.das.ha;

import cn.rongcapital.chorus.das.util.TestConfig;
import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@ComponentScan(basePackages = {"cn.rongcapital.chorus.das.ha"})
@MapperScan(basePackages = {"cn.rongcapital.chorus.das.ha.dao"}, sqlSessionFactoryRef = DBTestConfig.SQL_SESSION_FACTORY_NAME)
@EnableConfigurationProperties
public class DBTestConfig {

    public static final String SQL_SESSION_FACTORY_NAME = "sessionFactoryChorus";
    public static final String TX_MANAGER = "txManagerChorus";

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertiesResolver() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean(name = "datasourceChorus")
    @Primary
    @ConfigurationProperties(prefix = "datasource.chorus")
    @DependsOn("propertiesResolver")
    public DataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        return dataSource;
    }

    @Bean(name = SQL_SESSION_FACTORY_NAME)
    @Primary
    @DependsOn(value = {"propertiesResolver"})
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource());
        return sessionFactoryBean.getObject();
    }

    @Bean(name = TX_MANAGER)
    @Primary
    @DependsOn("propertiesResolver")
    public DataSourceTransactionManager transactionManager() {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource());
        return transactionManager;
    }
}
