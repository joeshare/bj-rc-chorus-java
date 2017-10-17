package cn.rongcapital.chorus.server.job.monitor.controller;

import cn.rongcapital.chorus.common.elasticsearch.JestClientAutoConfiguration;
import cn.rongcapital.chorus.common.xd.XDClient;
import cn.rongcapital.chorus.das.dao.JobMapper;
import cn.rongcapital.chorus.das.dao.JobMonitorMapper;
import cn.rongcapital.chorus.das.dao.TaskMapper;
import cn.rongcapital.chorus.das.service.JobMonitorService;
import cn.rongcapital.chorus.das.service.JobService;
import cn.rongcapital.chorus.das.service.impl.JobMonitorServiceImpl;
import cn.rongcapital.chorus.das.xd.dao.XDMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.PropertyPlaceholderAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * @author yimin
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JobMonitorController.class, JobMonitorControllerIT.class, JestClientAutoConfiguration.class, PropertyPlaceholderAutoConfiguration.class})
@Configuration
@WebAppConfiguration
@EnableWebMvc
@TestPropertySource(properties = {
        "elastic.execution.index.typs=logs",
        "elastic.execution.index.names=kafka_to_elk",
        "service.elasticsearch.uris=http://10.200.48.156:9200,http://10.200.48.157:9200,http://10.200.48.158:9200"
})
public class JobMonitorControllerIT {
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
        executionId = 7131L;
        mockMvc.perform(get("/{userId}/{projectId}/job/{executionId}/log/tailf", "19", 20, executionId))
               .andExpect(status().isOk())//;
               .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void realTimeLogThroughPostData() throws Exception {
        executionId = 7131L;
        mockMvc.perform(post("/{userId}/{projectId}/job/{executionId}/log/tailf", "19", 20, executionId)
                                .header("Content-Type","application/json").content("{\"lastTimestamp\":1504074057000}"))
               .andExpect(status().isOk())//;
               .andDo(MockMvcResultHandlers.print());
    }

    @Bean
    JobService jobService() {
        return Mockito.mock(JobService.class);
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
    public static XDClient xdClient() {
        return Mockito.mock(XDClient.class);
    }
}
