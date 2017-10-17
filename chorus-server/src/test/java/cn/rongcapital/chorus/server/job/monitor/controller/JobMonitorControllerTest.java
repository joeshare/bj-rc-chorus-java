package cn.rongcapital.chorus.server.job.monitor.controller;

import cn.rongcapital.chorus.das.bo.LogBO;
import cn.rongcapital.chorus.das.service.JobMonitorService;
import cn.rongcapital.chorus.das.service.JobService;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * @author yimin
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JobMonitorController.class, JobMonitorControllerTest.class})
@Configuration
@WebAppConfiguration
@EnableWebMvc
public class JobMonitorControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;
    private long    lastTimestamp;
    private long    executionId;

    @Before
    public void setUp() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void realTimeLog() throws Exception {
        executionId = 201L;
        mockMvc.perform(get("/{userId}/{projectId}/job/{executionId}/log/tailf", "19", 20, executionId))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.data.end", is(1503651722960L)))
               .andExpect(jsonPath("$.data.start", is(0)))
               .andExpect(jsonPath("$.data.total", is(4)));
    }

    @Bean
    JobMonitorService jobMonitorService() throws IOException {
        final JobMonitorService mock = Mockito.mock(JobMonitorService.class);
        List<String> messages = ImmutableList.of("2017-08-25T17:02:02+0800 1.3.3-SNAPSHOT WARN task-scheduler-9 kafka.KafkaMessageBus$SendingHandler - send to topic  :tap_3Ajob_3Achorus_5FzRfwlZWtPiEnJzHYJjdm1503369854328.job",
                                                 "2017-08-25T17:02:02+0800 1.3.3-SNAPSHOT WARN task-scheduler-9 kafka.KafkaMessageBus$SendingHandler - sending message:GenericMessage [payload=JobExecution: id=6686, version=1, startTime=Fri Aug 25 17:02:02 CST 2017, endTime=null, lastUpdated=Fri Aug 25 17:02:02 CST 2017, status=STARTED, exitStatus=exitCode=UNKNOWN;exitDescription=, job=[JobInstance: id=6655, version=0, Job=[chorus_zRfwlZWtPiEnJzHYJjdm1503369854328]], jobParameters=[{random=0.08777911627408297}], headers={id=b464e662-7e99-93b7-aac9-bd7a5e701603, timestamp=1503651722852}]",
                                                 "2017-08-25T17:02:02+0800 1.3.3-SNAPSHOT WARN task-scheduler-9 kafka.KafkaMessageBus$SendingHandler - sending message:GenericMessage [payload=JobExecution: id=6686, version=1, startTime=Fri Aug 25 17:02:02 CST 2017, endTime=null, lastUpdated=Fri Aug 25 17:02:02 CST 2017, status=STARTED, exitStatus=exitCode=UNKNOWN;exitDescription=, job=[JobInstance: id=6655, version=0, Job=[chorus_zRfwlZWtPiEnJzHYJjdm1503369854328]], jobParameters=[{random=0.08777911627408297}], headers={id=b464e662-7e99-93b7-aac9-bd7a5e701603, timestamp=1503651722852}]",
                                                 "2017-08-25T17:02:02+0800 1.3.3-SNAPSHOT WARN task-scheduler-9 kafka.KafkaMessageBus$SendingHandler - send to topic  :tap_3Ajob_3Achorus_5FzRfwlZWtPiEnJzHYJjdm1503369854328");
        LogBO logBO= LogBO.builder().start(lastTimestamp).end(1503651722960L).lines(messages).total(4).build();
        when(mock.realTimeLog(anyLong(), anyLong(), anyInt(), anyInt())).thenReturn(logBO);
        return mock;
    }

    @Bean
    JobService jobService() {
        return Mockito.mock(JobService.class);
    }

}
