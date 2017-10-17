package cn.rongcapital.chorus.server.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.pagehelper.PageHelper;

/**
 * Mybatis相关配置
 *
 * @author li.hzh
 * @date 2016-02-29
 */
@Configuration
@MapperScan(basePackages={"cn.rongcapital.chorus.core.*.repository","cn.rongcapital.chorus.das.dao"},sqlSessionFactoryRef=MybatisConfig.SQL_SESSION_FACTORY_NAME)
public class MybatisConfig {
    public static final String SQL_SESSION_FACTORY_NAME = "sessionFactoryChorus";
    public static final String TX_MANAGER = "txManagerChorus";

    private Interceptor pageHelper;

    @Bean(name = "datasourceChorus")
    @Primary
    @ConfigurationProperties(prefix = "datasource.chorus")
    public DataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        return dataSource;
    }

    @Bean(name = SQL_SESSION_FACTORY_NAME)
    @Primary
    @DependsOn(value = "pageHelper")
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource());
        sessionFactoryBean.setPlugins(new Interceptor[]{pageHelper});
        return sessionFactoryBean.getObject();
    }

    @Bean(name = TX_MANAGER)
    @Primary
    public DataSourceTransactionManager transactionManager() {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource());
        return transactionManager;
    }

    /**
     * MyBatis分页插件
     */
    @Bean(name = "pageHelper")
    public Interceptor pageHelper() {
        pageHelper = new PageHelper();
        Properties properties = new Properties();
        properties.setProperty("dialect", "mysql");
        pageHelper.setProperties(properties);
        return pageHelper;
    }
}
