package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.common.xd.XDClient;
import cn.rongcapital.chorus.das.bo.LogBO;
import cn.rongcapital.chorus.das.dao.JobMapper;
import cn.rongcapital.chorus.das.dao.JobMonitorMapper;
import cn.rongcapital.chorus.das.dao.TaskMapper;
import cn.rongcapital.chorus.das.service.JobMonitorService;
import cn.rongcapital.chorus.das.service.impl.JobMonitorServiceImpl;
import cn.rongcapital.chorus.das.xd.dao.XDMapper;
import io.searchbox.action.Action;
import io.searchbox.client.JestClient;
import io.searchbox.core.SearchResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.PropertyPlaceholderAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.Collection;

import static org.apache.commons.io.FileUtils.getFile;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * @author yimin
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Configuration
@ContextConfiguration(classes = {JobMonitorServiceImplTest.class, PropertyPlaceholderAutoConfiguration.class})
@TestPropertySource(properties = {
        "elastic.execution.index.typs=status",
        "elastic.execution.index.names=kafka-index"
})
@Profile({"single"})
public class JobMonitorServiceImplTest {

    @Autowired
    JobMonitorService jobMonitorService;
    @Value("${elastic.execution.index.typs}")
    private Collection<String> indexTypes;
    @Value("${elastic.execution.index.names}")
    private Collection<String> indexNames;

    @Test
    public void listBinding() throws Exception {
        assertThat(indexTypes).hasSize(1);
        assertThat(indexTypes).containsExactly("status");
        assertThat(indexNames).hasSize(1);
        assertThat(indexNames).containsExactly("kafka-index");
    }

    @Test
    public void realTimeLog() throws Exception {
        long executionId = 12L;
        long startTimestamp = System.currentTimeMillis();

        final LogBO logBO = jobMonitorService.realTimeLog(executionId, startTimestamp, 0, 1000);
        assertThat(logBO.getTotal()).isEqualTo(4);
        assertThat(logBO.getStart()).isEqualTo(startTimestamp);
        assertThat(logBO.getEnd()).isEqualTo(1503651722960L);
        assertThat(logBO.getLines()).hasSize(4);
        assertThat(logBO.getLines()).containsExactly("2017-08-25T17:02:02+0800 1.3.3-SNAPSHOT WARN task-scheduler-9 kafka.KafkaMessageBus$SendingHandler - send to topic  :tap_3Ajob_3Achorus_5FzRfwlZWtPiEnJzHYJjdm1503369854328.job",
                                                     "2017-08-25T17:02:02+0800 1.3.3-SNAPSHOT WARN task-scheduler-9 kafka.KafkaMessageBus$SendingHandler - sending message:GenericMessage [payload=JobExecution: id=6686, version=1, startTime=Fri Aug 25 17:02:02 CST 2017, endTime=null, lastUpdated=Fri Aug 25 17:02:02 CST 2017, status=STARTED, exitStatus=exitCode=UNKNOWN;exitDescription=, job=[JobInstance: id=6655, version=0, Job=[chorus_zRfwlZWtPiEnJzHYJjdm1503369854328]], jobParameters=[{random=0.08777911627408297}], headers={id=b464e662-7e99-93b7-aac9-bd7a5e701603, timestamp=1503651722852}]",
                                                     "2017-08-25T17:02:02+0800 1.3.3-SNAPSHOT WARN task-scheduler-9 kafka.KafkaMessageBus$SendingHandler - sending message:GenericMessage [payload=JobExecution: id=6686, version=1, startTime=Fri Aug 25 17:02:02 CST 2017, endTime=null, lastUpdated=Fri Aug 25 17:02:02 CST 2017, status=STARTED, exitStatus=exitCode=UNKNOWN;exitDescription=, job=[JobInstance: id=6655, version=0, Job=[chorus_zRfwlZWtPiEnJzHYJjdm1503369854328]], jobParameters=[{random=0.08777911627408297}], headers={id=b464e662-7e99-93b7-aac9-bd7a5e701603, timestamp=1503651722852}]",
                                                     "2017-08-25T17:02:02+0800 1.3.3-SNAPSHOT WARN task-scheduler-9 kafka.KafkaMessageBus$SendingHandler - send to topic  :tap_3Ajob_3Achorus_5FzRfwlZWtPiEnJzHYJjdm1503369854328");

    }

    @Bean
    public static JobMonitorService jobMonitorServiceImp() {
        return new JobMonitorServiceImpl();
    }

    @Bean
    public static JobMonitorMapper jobMonitorMapper() {
        return Mockito.mock(JobMonitorMapper.class);
    }

    @Bean
    public static JobMapper jobMapper() {
        return Mockito.mock(JobMapper.class);
    }

    @Bean
    public static TaskMapper taskMapper() {
        return Mockito.mock(TaskMapper.class);
    }

    @Bean
    public static XDMapper xdMapper() {
        return Mockito.mock(XDMapper.class);
    }

    @Bean
    public static JestClient jestClient() throws IOException {
        final JestClient mock = Mockito.mock(JestClient.class);
        SearchResult searchResult = Mockito.mock(SearchResult.class);
        when(mock.execute(any(Action.class))).thenReturn(searchResult);
        when(searchResult.getTotal()).thenReturn(4);
        when(searchResult.getJsonString()).thenReturn(readFileToString(getFile(JobMonitorServiceImplTest.class.getClassLoader().getResource("cn/rongcapital/chorus/das/service/impl/elastic-job-execution-log.json").getFile())));
        return mock;
    }

    @Bean
    public static XDClient xdClient() {
        return Mockito.mock(XDClient.class);
    }
}
