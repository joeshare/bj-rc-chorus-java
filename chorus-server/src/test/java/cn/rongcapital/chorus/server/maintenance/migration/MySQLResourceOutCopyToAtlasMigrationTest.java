package cn.rongcapital.chorus.server.maintenance.migration;

import cn.rongcapital.chorus.das.entity.ResourceOut;
import cn.rongcapital.chorus.das.service.ResourceOutService;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
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
import java.util.Date;
import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;
import static org.mockito.internal.matchers.NotNull.NOT_NULL;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * @author yimin
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {MySQLResourceOutCopyToAtlasMigration.class, MySQLResourceOutCopyToAtlasMigrationTest.class})
@Configuration
@WebAppConfiguration
@EnableWebMvc
public class MySQLResourceOutCopyToAtlasMigrationTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void mysqlTypeToAtlas() throws Exception {
        mockMvc.perform(post("/maintenance/migration/mysql-to-atlas"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.data[0].guid", NOT_NULL ));
    }

    @Bean
    ResourceOutService resourceOutService() {
        final ResourceOutService mock = Mockito.mock(ResourceOutService.class);
        List<ResourceOut> mysqlUnSyncedResource = new ArrayList<>();
        ResourceOut resource = new ResourceOut();
        resource.setResourceOutId(RandomUtils.nextLong(1, 1000));
        resource.setProjectId(222667L);
        resource.setResourceName("chorus");
        resource.setResourceDesc("chorus");
        resource.setStorageType("1");
        resource.setConnUrl("jdbc:mysql://10.200.48.79:3306/chorus");
        resource.setConnPort("3306");
        resource.setConnUser("dps");
        resource.setConnPass("Dps@10.200.48.MySQL");
        resource.setCreateUserId("10970");
        resource.setCreateTime(new Date());
        resource.setDatabaseName("chorus");
        resource.setConnHost("10.200.48.79");
        resource.setCreateUserName("guoyemeng");
        resource.setGuid("");
        mysqlUnSyncedResource.add(resource);
        when(mock.getUnSyncedMySQLResource(anyInt())).thenReturn(mysqlUnSyncedResource);
        when(mock.syncToMetaDataCenter(any(ResourceOut.class))).then((Answer<ResourceOut>) invocation -> {
            ResourceOut resourceOut = (ResourceOut) invocation.getArguments()[0];
            resourceOut.setGuid(randomAlphabetic(10));
            return resourceOut;
        });
        return mock;
    }
}
