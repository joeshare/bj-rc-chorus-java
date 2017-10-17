package cn.rongcapital.chorus.server.config;

import cn.rongcapital.chorus.das.service.common.definition.bean.ConnectorConfigInfo;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 应用全局配置
 *
 * @author maboxiao
 * @date 2016-12-5
 */
@Configuration
public class ConnectorConfig {
   
    @Bean
    @ConfigurationProperties(prefix = "conn")
    public ConnectorConfigInfo connectorConfigInfo() {
    	ConnectorConfigInfo connectorConfigInfo = new ConnectorConfigInfo();
        return connectorConfigInfo;
    }

}
