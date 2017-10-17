package cn.rongcapital.chorus.das.util;

import cn.rongcapital.chorus.common.xd.model.XDAdminConfig;
import cn.rongcapital.chorus.common.zk.ZKConfig;
import cn.rongcapital.chorus.das.service.LoggerFactory;
import cn.rongcapital.chorus.das.service.common.definition.bean.ConnectorConfigInfo;
import com.alibaba.druid.pool.DruidDataSource;
import com.github.pagehelper.PageHelper;
import org.apache.atlas.ApplicationProperties;
import org.apache.atlas.AtlasClientV2;
import org.apache.atlas.AtlasException;
import org.apache.hadoop.yarn.client.api.YarnClient;
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
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.sql.DataSource;
import java.util.Properties;

import static org.apache.atlas.ApplicationProperties.ATLAS_CONFIGURATION_DIRECTORY_PROPERTY;

/**
 * Mybatis相关配置
 *
 * @author li.hzh
 * @date 2016-02-29
 */
@Configuration
@ComponentScan(basePackages = {"cn.rongcapital.chorus.das","cn.rongcapital.chorus.governance","cn.rongcapital.chorus.common","cn.rongcapital.chorus.authorization"}, excludeFilters = {})
@MapperScan(basePackages = { "cn.rongcapital.chorus.core.*.repository", "cn.rongcapital.chorus.das.dao",
		"cn.rongcapital.chorus.das.xd.dao" }, sqlSessionFactoryRef = TestConfig.SQL_SESSION_FACTORY_NAME)
@EnableConfigurationProperties
@ImportResource("classpath:hadoop-conf.xml")
public class TestConfig {
	public static final String SQL_SESSION_FACTORY_NAME = "sessionFactoryChorus";
	public static final String TX_MANAGER = "txManagerChorus";
	public static final String ATLAS_REST_ADDRESS = "atlas.rest.address";

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
		sessionFactoryBean.setPlugins(new Interceptor[]{pageHelper});
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

	@Bean
	public AtlasClientV2 atlasClientV2() throws AtlasException {

		System.setProperty(ATLAS_CONFIGURATION_DIRECTORY_PROPERTY, "src/test/resources");
		ApplicationProperties.get().setProperty("atlas.client.readTimeoutMSecs", "100000000");
		ApplicationProperties.get().setProperty("atlas.client.connectTimeoutMSecs", "100000000");

		org.apache.commons.configuration.Configuration configuration = ApplicationProperties.get();
		String[] atlasUrls = configuration.getStringArray(ATLAS_REST_ADDRESS);

		AtlasClientV2 atlasClientV2 = new AtlasClientV2(atlasUrls, new String[]{"admin", "admin"});
		return atlasClientV2;
	}

	@Bean
	public YarnClient client() throws AtlasException {
		YarnClient client = YarnClient.createYarnClient();
		return client;
	}
	
    @Bean
    public LoggerFactory loggerFactory(@Value("${logger.name.audit.table:" + "table-operations-audit" + "}") String loggerName) {
        return () -> org.slf4j.LoggerFactory.getLogger(loggerName);
    }
    /**
     * mail sender
     * 
     * @return
     * @author yunzhong
     * @version
     * @since 2017年5月16日
     */
    @Bean
    @ConfigurationProperties(prefix = "mail")
    public JavaMailSender javaMailServer() {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        Properties javaMailProperties = new Properties();
        javaMailProperties.setProperty("mail.smtp.auth", "true");
        javaMailProperties.setProperty("mail.smtp.timeout", "25000");
        sender.setJavaMailProperties(javaMailProperties);
        return sender;
    }

    /**
     * @return
     * @author yunzhong
     * @version
     * @since 2017年5月17日
     */
    @Bean
    public FreeMarkerConfigurer freeMarkerConfigurer() {
        FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
        String templateLoaderPath = "classpath:/ftltemplates/";
        configurer.setTemplateLoaderPath(templateLoaderPath);
        Properties settings = new Properties();
        settings.setProperty("output_encoding", "UTF-8");
        settings.setProperty("locale", "zh_CN");
        settings.setProperty("datetime_format", "yyyy-MM-dd HH:mm:ss");
        configurer.setFreemarkerSettings(settings);
        configurer.setDefaultEncoding("UTF-8");
        return configurer;
    }
}
