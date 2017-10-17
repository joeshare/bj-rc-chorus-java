package cn.rongcapital.chorus.common.elasticsearch;

import cn.rongcapital.chorus.common.exception.ServiceException;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static cn.rongcapital.chorus.common.constant.StatusCode.ELASTICSEARCH_SERVICE_NOT_FOUND;

/**
 * @author yimin
 */
@Slf4j
@Configuration
public class JestClientAutoConfiguration {
    private final int MAX_TOTAL_CONNECTION                   = 20;
    private final int DEFAULT_MAX_TOTAL_CONNECTION_PER_ROUTE = 2;
    @Value("${jestclient.timeout}")
    private int jctimeout;

    // Construct a new Jest client according to configuration via factory
    @Bean
    public JestClientFactory jestClientFactory(@Nonnull @Value("${service.elasticsearch.uris}") final String[] serverUris) {
        log.info("service.elasticsearch.uris={}", serverUris);

        final List<String> validUris = Arrays.stream(serverUris).filter(StringUtils::isNotBlank).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(validUris)) {
            log.warn("any elasticSearch server not found");
            throw new ServiceException(ELASTICSEARCH_SERVICE_NOT_FOUND);
        }

        final JestClientFactory jestClientFactory = new JestClientFactory();
        jestClientFactory.setHttpClientConfig(new HttpClientConfig.Builder(validUris)
                                                      .multiThreaded(true)
                                                      //Per default this implementation will create no more than 2 concurrent connections per given route
                                                      .defaultMaxTotalConnectionPerRoute(DEFAULT_MAX_TOTAL_CONNECTION_PER_ROUTE)
                                                      // and no more 20 connections in total
                                                      .maxTotalConnection(MAX_TOTAL_CONNECTION)
                                                      .readTimeout(jctimeout)
                                                      .build());
        return jestClientFactory;
    }

    @Bean(destroyMethod = "shutdownClient")
    public JestClient jestClient(JestClientFactory jestClientFactory) {
        return jestClientFactory.getObject();
    }
}
