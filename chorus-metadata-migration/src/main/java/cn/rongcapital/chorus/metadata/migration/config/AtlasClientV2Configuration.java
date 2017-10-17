package cn.rongcapital.chorus.metadata.migration.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.atlas.AtlasClientV2;
import org.apache.hadoop.security.UserGroupInformation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.IOException;

/**
 * @author yimin
 */
@Slf4j
@Configuration
@PropertySource(name = "atlas-application", value = {"classpath:atlas-application.properties"})
public class AtlasClientV2Configuration {

    @Value("${atlas.rest.address}")
    private String[] atlasEndpoint;
    @Value("${atlas.rest.user.password}")
    private String[] userAndPassword;


    @Bean
    @ConditionalOnMissingBean(AtlasClientV2.class)
    public AtlasClientV2 atlasClientV2() throws IOException {
        log.info("atlas client v2 url:{} " + atlasEndpoint[0]);
        return new AtlasClientV2(atlasEndpoint, userAndPassword);
    }
}
