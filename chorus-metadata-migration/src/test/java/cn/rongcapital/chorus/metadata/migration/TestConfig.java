package cn.rongcapital.chorus.metadata.migration;

import cn.rongcapital.chorus.common.xd.model.XDAdminConfig;
import cn.rongcapital.chorus.common.zk.ZKConfig;
import cn.rongcapital.chorus.das.service.common.definition.bean.ConnectorConfigInfo;
import com.alibaba.druid.pool.DruidDataSource;
import com.github.pagehelper.PageHelper;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Mybatis相关配置
 *
 * @author li.hzh
 * @date 2016-02-29
 */
@Configuration
@ComponentScan(basePackages = "cn.rongcapital.chorus", excludeFilters = {})
@MapperScan(basePackages = { "cn.rongcapital.chorus.core.*.repository", "cn.rongcapital.chorus.das.dao" }, sqlSessionFactoryRef = TestConfig.SQL_SESSION_FACTORY_NAME)
@EnableConfigurationProperties
public class TestConfig {
	public static final String SQL_SESSION_FACTORY_NAME = "sessionFactoryChorus";
	public static final String TX_MANAGER = "txManagerChorus";

	private Interceptor pageHelper;

	@Value("${zookeeper.address}")
	private String address;
	
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
	@DependsOn(value = {"pageHelper","propertiesResolver"})
	public SqlSessionFactory sqlSessionFactory() throws Exception {
		SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
		sessionFactoryBean.setDataSource(dataSource());
		sessionFactoryBean.setPlugins(new Interceptor[] { pageHelper });
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

	/**
	 * MyBatis分页插件
	 */
	@Bean(name = "pageHelper")
	@DependsOn("propertiesResolver")
	public Interceptor pageHelper() {
		pageHelper = new PageHelper();
		Properties properties = new Properties();
		properties.setProperty("dialect", "mysql");
		pageHelper.setProperties(properties);
		return pageHelper;
	}

	@Bean
	@ConfigurationProperties(prefix = "zookeeper")
	@DependsOn("propertiesResolver")
	public ZKConfig zkConfig() {
		ZKConfig zkConfig = new ZKConfig();
		return zkConfig;
	}
	
    @Bean
    @ConfigurationProperties(prefix = "xd")
    public XDAdminConfig xdAdminConfig() {
        XDAdminConfig xdAdminConfig = new XDAdminConfig();
        return xdAdminConfig;
    }
    
    @Bean
    @ConfigurationProperties(prefix = "conn")
    public ConnectorConfigInfo connectorConfigInfo() {
    	ConnectorConfigInfo connectorConfigInfo = new ConnectorConfigInfo();
        return connectorConfigInfo;
    }
}
