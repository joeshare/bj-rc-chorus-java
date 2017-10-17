package cn.rongcapital.chorus.server.platform;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.das.entity.UnexecutedJob;
import cn.rongcapital.chorus.das.service.PlatformMaintenanceService;
import cn.rongcapital.chorus.server.platform.controller.PlatformMaintenanceController;

/**
 * @author kevin.gong
 * @Time 2017年9月19日 下午3:33:42
 */
@RunWith(PowerMockRunner.class)
@PowerMockIgnore("javax.management.*")
public class PlatformMaintenanceControllerTest {
    private MockMvc mockMvc;
    
    @InjectMocks
    private PlatformMaintenanceController platformMaintenanceController;
    
    @Mock
    private PlatformMaintenanceService platformMaintenanceService;



    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(platformMaintenanceController).build();
    }
    
    @Test
    public void testSetPlatformMaintenanceStatus() throws Exception {
        when(platformMaintenanceService.getPlatformMaintenanceStatus()).thenReturn(0);
        mockMvc.perform(post("/platform/maintenance/status").param("status", "0").contentType(MediaType.APPLICATION_JSON_UTF8))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.code").value(StatusCode.PLATFORM_MAINTAINED_ERROR.getCode()))
            ;
        
        when(platformMaintenanceService.setPlatformMaintenanceStatus(1)).thenReturn(true);
        mockMvc.perform(post("/platform/maintenance/status").param("status", "1").contentType(MediaType.APPLICATION_JSON_UTF8))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
          .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS.getCode()))
          ;
        
        when(platformMaintenanceService.getPlatformMaintenanceStatus()).thenReturn(1);
        mockMvc.perform(post("/platform/maintenance/status").param("status", "1").contentType(MediaType.APPLICATION_JSON_UTF8))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.code").value(StatusCode.PLATFORM_STARTED_ERROR.getCode()))
            ;
    }
    
    @Test
    public void testGetPlatformMaintenanceStatus() throws Exception {
        when(platformMaintenanceService.getPlatformMaintenanceStatus()).thenReturn(0);
        mockMvc.perform(get("/platform/maintenance/status").contentType(MediaType.APPLICATION_JSON_UTF8))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS.getCode()))
            ;
    }
    
    @Test
    public void testGetUnexecutedJobList() throws Exception {
        List<UnexecutedJob> ujs = new ArrayList<>();
        UnexecutedJob uj1 = new UnexecutedJob();
        uj1.setCreateUserName("wyz");
        uj1.setDescription("w_test_job");
        uj1.setJobName("job1");
        uj1.setProjectName("wyz_pro");
        uj1.setScheExecTime("2017-12-12 00:00:00");
        UnexecutedJob uj2 = new UnexecutedJob();
        uj2.setCreateUserName("gl");
        uj2.setDescription("g_test_job");
        uj2.setJobName("job2");
        uj2.setProjectName("lin_pro");
        uj2.setScheExecTime("2017-12-12 01:00:00");
        ujs.add(uj1);
        ujs.add(uj2);
        when(platformMaintenanceService.getWaitExecuteJobsCount()).thenReturn(10);
        when(platformMaintenanceService.getWaitExecuteJobs(1, 2)).thenReturn(ujs);
        mockMvc.perform(get("/platform/maintenance/unexecuted_job/1/2").contentType(MediaType.APPLICATION_JSON_UTF8))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS.getCode()))
            ;
    }

}