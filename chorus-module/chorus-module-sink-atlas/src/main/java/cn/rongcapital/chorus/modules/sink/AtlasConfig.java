package cn.rongcapital.chorus.modules.sink;

import lombok.extern.slf4j.Slf4j;
import org.apache.atlas.AtlasClientV2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.IOException;

/**
 * Created by abiton on 10/08/2017.
 */
@Configuration
@PropertySource("config/atlas_sink.properties")
@Slf4j
public class AtlasConfig {
    @Value("${atlas.rest.address}")
    private String[] atlasEndpoint;

    @Value("${atlas.username}")
    private String username;
    @Value("${atlas.password}")
    private String password;
    @Bean
    public AtlasClientV2 atlasClientV2() throws IOException {
        try {
            return new AtlasClientV2(atlasEndpoint, new String[]{username,password});
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }
}
