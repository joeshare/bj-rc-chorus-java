package cn.rongcapital.chorus.server.config;

import cn.rongcapital.chorus.das.service.common.definition.bean.HiveConnConfigInfo;
import cn.rongcapital.chorus.common.xd.model.XDAdminConfig;
import cn.rongcapital.chorus.common.zk.ZKConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 应用全局配置
 *
 * @author li.hzh
 * @date 2016-02-29
 */
@Configuration
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class,
        ThymeleafAutoConfiguration.class, FreeMarkerAutoConfiguration.class, SecurityAutoConfiguration.class})
@ComponentScan(basePackages = "cn.rongcapital.chorus", excludeFilters = {})
@EnableSwagger2
@ImportResource("/config/hadoop-conf.xml")
@EnableScheduling
@Lazy(false)
public class ApplicationConfig {

    @Value("${hadoop.home.dir}")
    private String hadoopHome;


    @Bean
    @ConfigurationProperties(prefix = "zookeeper")
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
