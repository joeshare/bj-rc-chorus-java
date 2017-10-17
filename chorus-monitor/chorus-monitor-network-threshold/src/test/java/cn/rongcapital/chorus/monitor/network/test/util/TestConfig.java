package cn.rongcapital.chorus.monitor.network.test.util;

import cn.rongcapital.chorus.common.hadoop.DefaultHadoopClient;
import cn.rongcapital.chorus.common.xd.DefaultXDClient;
import cn.rongcapital.chorus.common.xd.autonconfigure.SpringXDTemplateDecoratorAutoConfiguration;
import cn.rongcapital.chorus.common.xd.model.XDAdminConfig;
import cn.rongcapital.chorus.common.xd.service.impl.DslGraphServiceImpl;
import cn.rongcapital.chorus.common.xd.service.impl.XDRuntimeServiceImpl;
import cn.rongcapital.chorus.common.zk.ZKConfig;
import cn.rongcapital.chorus.das.service.*;
import cn.rongcapital.chorus.das.service.common.definition.bean.ConnectorConfigInfo;
import cn.rongcapital.chorus.das.service.impl.AtlasEntityFactory;
import cn.rongcapital.chorus.governance.autoconfigure.AtlasClientV2AutoConfiguration;
import com.alibaba.druid.pool.DruidDataSource;
import com.github.pagehelper.PageHelper;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Mybatis相关配置
 *
 * @author li.hzh
 * @date 2016-02-29
 */
@ComponentScan(basePackages = {"cn.rongcapital.chorus.monitor", "cn.rongcapital.chorus.common", "cn.rongcapital.chorus.das", "cn.rongcapital.chorus.authorization.plugin.ranger"},
        excludeFilters = {@ComponentScan.Filter(value =
                {DefaultHadoopClient.class, SpringXDTemplateDecoratorAutoConfiguration.class,
                        DefaultXDClient.class, DslGraphServiceImpl.class, XDRuntimeServiceImpl.class,
                        AtlasClientV2AutoConfiguration.class, ColumnInfoServiceV2.class,
                        ApplyDetailServiceV2.class, HiveTableInfoServiceV2.class,
                        TableInfoServiceV2.class, ApplyFormServiceV2.class, AtlasEntityFactory.class,
                        HdfsSnapshotService.class, HiveTableInfoService.class, JobDeploymentService.class,
                        JobMonitorService.class, ProjectResourceKpiSnapshotService.class,
                        QueueService.class, ResourceOperationService.class, TableAuthorityServiceV2.class,
                        HiveTableInfoService.class, TableInfoService.class, TableLinageService.class,
                        TableLinageServiceV2.class, TableMonitorServiceV2.class, ExternalDataSourceService.class,
                        ResourceOutService.class},
                type = FilterType.ASSIGNABLE_TYPE)})
@EnableConfigurationProperties
@EnableScheduling
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
    @DependsOn(value = {"pageHelper", "propertiesResolver"})
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

    @Bean(name = "hiveDataSource")
    @ConfigurationProperties(prefix = "hive.jdbc")
    public SimpleDriverDataSource druidHiveDataSource() {
        return new SimpleDriverDataSource();
    }

    @Bean
    @ConfigurationProperties(prefix = "conn")
    public ConnectorConfigInfo connectorConfigInfo() {
        ConnectorConfigInfo connectorConfigInfo = new ConnectorConfigInfo();
        return connectorConfigInfo;
    }

}
