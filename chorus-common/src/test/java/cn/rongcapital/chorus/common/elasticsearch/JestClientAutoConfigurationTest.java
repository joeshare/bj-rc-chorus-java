package cn.rongcapital.chorus.common.elasticsearch;

import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.common.exception.ServiceException;
import cn.rongcapital.chorus.common.test.AnnotationConfigApplicationContextTestUtils;
import com.alibaba.fastjson.JSONPath;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.search.sort.Sort;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.autoconfigure.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * @author yimin
 */
@SpringApplicationConfiguration
public class JestClientAutoConfigurationTest {
//    @Rule
//    public MockServerRule mockServerRule = new MockServerRule(this);

    @Test(expected = BeanCreationException.class)
    public void whenElasticServiceNotConfiguredThrowException() throws Exception {
        try {
            applicationContextLauncher("service.elasticsearch.uris=");
        } catch (BeanCreationException e) {
            final Throwable rootCause = e.getRootCause();
            assertThat(rootCause).isInstanceOf(ServiceException.class);
            ServiceException exception = (ServiceException) rootCause;
            assertThat(exception.getCode()).isEqualTo(StatusCode.ELASTICSEARCH_SERVICE_NOT_FOUND.getCode());
            assertThat(exception.getMessage()).isEqualTo(StatusCode.ELASTICSEARCH_SERVICE_NOT_FOUND.getDesc());
            throw e;
        } catch (Exception e) {
            fail("show be BeanCreationException");
        }
        fail("config service.elasticsearch.uris= not right");
    }

    @Test
    public void getJestClient() throws Exception {
        ApplicationContext applicationContext = applicationContextLauncher("service.elasticsearch.uris=localhost:18080,localhost:18081");
        applicationContext.getBean(JestClientFactory.class);
        applicationContext.getBean(JestClient.class);
    }

    @Ignore(value = "This is a integration test")
    public void jestClientUsage() throws Exception {
        ApplicationContext applicationContext = applicationContextLauncher(
                "service.elasticsearch.uris=http://10.200.48.156:9200,http://10.200.48.157:9200,http://10.200.48.158:9200");
        final JestClient jestClient = applicationContext.getBean(JestClient.class);
        String query = "{\"query\":{\"bool\":{\"must\":[{\"term\":{\"executionId\":\"7129\"}},{\"range\":{\"timestamp\":{\"gt\":\"1503985726993\",\"lt\":\"1503985726993\"}}}]}},\"from\":0,\"size\":10}";
        Search search = new Search.Builder(query).addSort(new Sort("timestamp", Sort.Sorting.ASC))
                                                 .addType("status")
                                                 .addIndex("kafka-index").build();
        final SearchResult result = jestClient.execute(search);
        System.out.println(result.getTotal());
        final List<String> messages = (List<String>) JSONPath.read(result.getJsonString(), "$.._source.message");
        final List<String> timestamp = (List<String>) JSONPath.read(result.getJsonString(), "$.._source.timestamp");
        messages.forEach(System.out::println);
        timestamp.forEach(System.out::println);
        final Long aLong = timestamp.stream().map(Long::valueOf).max(Long::compareTo).get();
        System.out.println("max timestamp : " + aLong);
    }

    private ApplicationContext applicationContextLauncher(String environmentPairs) {
        return AnnotationConfigApplicationContextTestUtils
                .applicationContextLauncher(
                        new Class[]{JestClientAutoConfiguration.class, PropertyPlaceholderAutoConfiguration.class},
                        environmentPairs
                );

    }
}
