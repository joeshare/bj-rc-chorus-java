package cn.rongcapital.chorus.metadata.migration.config;

import cn.rongcapital.chorus.common.xd.model.XDAdminConfig;
import cn.rongcapital.chorus.das.service.common.definition.bean.HiveConnConfigInfo;
import cn.rongcapital.chorus.das.service.impl.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.*;

/**
 * 应用全局配置
 *
 * @author li.hzh
 * @date 2016-02-29
 */
@Configuration
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class,
        ThymeleafAutoConfiguration.class, FreeMarkerAutoConfiguration.class, SecurityAutoConfiguration.class} )
@ComponentScan(basePackages = "cn.rongcapital.chorus.governance,cn.rongcapital.chorus.metadata.migration",
        basePackageClasses = {AtlasEntityFactory.class, MetadataServiceImpl.class, ProjectInfoServiceImpl.class, ColumnInfoServiceImpl.class}
       , excludeFilters = {@ComponentScan.Filter(type = FilterType.REGEX, pattern = "cn.rongcapital.chorus.governance.autoconfigure.AtlasClientV2AutoConfiguration|cn.rongcapital.chorus.das.service.impl.*OperationServiceImpl|cn.rongcapital.chorus.das.service.impl.*QueueServiceImpl|cn.rongcapital.chorus.das.service.impl.*MonitorServiceImpl|cn.rongcapital.chorus.das.service.impl.*DeploymentServiceImpl|cn.rongcapital.chorus.das.service.impl.*GraphDataServiceImpl|cn.rongcapital.chorus.das.service.impl.*GraphServiceImpl|cn.rongcapital.chorus.das.service.impl.*GraphInfoServiceImpl|cn.rongcapital.chorus.das.service.impl.*DataToGraphDBServiceImpl|cn.rongcapital.chorus.das.service.impl.*QueryServiceImpl")}
        )
@Lazy(false)
public class ApplicationConfig {

    @Value("${hadoop.home.dir}")
    private String hadoopHome;

    @Bean
    @ConfigurationProperties(prefix = "xd")
    public XDAdminConfig xdAdminConfig() {
        XDAdminConfig xdAdminConfig = new XDAdminConfig();
        return xdAdminConfig;
    }

    @Bean(name = "setHadoopHome")
    public String setHadoopHome() {
        return System.setProperty("hadoop.home.dir", hadoopHome);
    }

    @Bean
    @ConfigurationProperties(prefix = "hive.jdbc")
    public HiveConnConfigInfo hiveConnConfigInfo() {
        HiveConnConfigInfo hiveConnConfigInfo = new HiveConnConfigInfo();
        return hiveConnConfigInfo;
    }
}
