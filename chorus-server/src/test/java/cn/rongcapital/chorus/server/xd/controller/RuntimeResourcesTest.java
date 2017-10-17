package cn.rongcapital.chorus.server.xd.controller;

import cn.rongcapital.chorus.common.xd.service.XDRuntimeService;
import cn.rongcapital.chorus.das.entity.InstanceInfo;
import cn.rongcapital.chorus.das.service.InstanceInfoService;
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
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.ArrayList;
import java.util.List;

import static cn.rongcapital.chorus.server.xd.controller.RuntimeResources.CONTAINERS_STATS;
import static cn.rongcapital.chorus.server.xd.controller.RuntimeResources.PROJECT_RESTFUL_TEMPLATE;
import static org.hamcrest.Matchers.is;
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
@ContextConfiguration(classes = {RuntimeResources.class, RuntimeResourcesTest.class})
@Configuration
@WebAppConfiguration
@EnableWebMvc
public class RuntimeResourcesTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void containersStats() throws Exception {
        mockMvc.perform(get(PROJECT_RESTFUL_TEMPLATE + CONTAINERS_STATS, 19))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.data.used", is(21)))
               .andExpect(jsonPath("$.data.remaining", is(29)))
               .andExpect(jsonPath("$.data.projectId", is(19)));
    }

    @Bean
    XDRuntimeService xdRuntimeService() {
        final XDRuntimeService mock = Mockito.mock(XDRuntimeService.class);
        when(mock.containersServiceStatusStats(anyLong())).thenReturn(new int[]{10, 21});
        return mock;
    }

    @Bean
    InstanceInfoService instanceInfoService() {
        final InstanceInfoService mock = Mockito.mock(InstanceInfoService.class);
        int total= 50;
        List<InstanceInfo> allInstance = new ArrayList<>(total);
        while (total-- > 0) {
            allInstance.add(new InstanceInfo());
        }
        when(mock.listByProject(19L)).thenReturn(allInstance);
        return mock;
    }

}
