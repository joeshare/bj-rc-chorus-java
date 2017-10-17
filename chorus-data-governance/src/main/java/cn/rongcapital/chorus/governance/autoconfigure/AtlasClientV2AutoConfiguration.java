package cn.rongcapital.chorus.governance.autoconfigure;

import lombok.extern.slf4j.Slf4j;
import org.apache.atlas.AtlasClientV2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
public class AtlasClientV2AutoConfiguration {

    @Value("${atlas.rest.address:http://localhost:21000/}")
    private String[] atlasEndpoint;

//    @Bean
//    @ConditionalOnMissingBean(AtlasClientV2.class)
//    @ConditionalOnProperty(name = "atlas.authentication.method.kerberos", havingValue = "true")
//    public AtlasClientV2 atlasClientV2(
//            @Value("atlas.authentication.kerberos.name") String name,
//            @Value("atlas.authentication.kerberos.password") String password
//    ) {
//        return new AtlasClientV2(atlasEndpoint, new String[]{name, password});
//    }

    @Bean
//    @ConditionalOnMissingBean(AtlasClientV2.class)
    public AtlasClientV2 atlasClientV2() throws IOException {
        // TODO support multi-tenant
        // UserGroupInformation ugi = UserGroupInformation.getCurrentUser();
        // log.info("atlas client v2 : " + ugi.toString());
        // return new AtlasClientV2(ugi, ugi.getShortUserName(), atlasEndpoint);
        try {
            return new AtlasClientV2(atlasEndpoint, new String[]{"admin", "admin"});
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(),e);
        }
        return null;
    }
}
