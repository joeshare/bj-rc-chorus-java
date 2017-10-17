package cn.rongcapital.chorus.server.test.dashboard.controller;

import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.common.hadoop.HadoopClient;
import cn.rongcapital.chorus.common.util.DateUtils;
import cn.rongcapital.chorus.das.entity.PlatformResourceSnapshot;
import cn.rongcapital.chorus.das.entity.TotalResource;
import cn.rongcapital.chorus.das.service.PlatformResourceSnapshotService;
import cn.rongcapital.chorus.das.service.ProjectResourceKpiSnapshotService;
import cn.rongcapital.chorus.resourcemanager.service.TotalResourceService;
import cn.rongcapital.chorus.server.dashboard.controller.PlatformManagementController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by Athletics on 2017/7/19.
 */
@RunWith(PowerMockRunner.class)
public class PlatformManagementControllerTest {

    private MockMvc mockMvc;
    @Mock
    private ProjectResourceKpiSnapshotService projectResourceKpiSnapshotService;
    @Mock
    private PlatformResourceSnapshotService platformResourceSnapshotService;
    @Mock
    private TotalResourceService totalResourceService;
    @Mock
    private HadoopClient hClient;

    @InjectMocks
    private PlatformManagementController platformManagementController;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(platformManagementController).build();
    }

    @Test
    public void getUseRateTeset() throws Exception{
        TotalResource total = new TotalResource();
        total.setCpu(100);
        total.setMemory(400);
        total.setStorage(5000);

        Map<String, Long> resourceMap = new HashMap<>();
        resourceMap.put("cpu",43L);
        resourceMap.put("memory",127L);
        resourceMap.put("storage",400L);

        when(projectResourceKpiSnapshotService.queryUseRate()).thenReturn(resourceMap);
        when(totalResourceService.getTotalResourceWithQueueCapacity()).thenReturn(total);
        mockMvc.perform(get("/dashboard/platform/use_rate"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data").isMap());
    }

    @Test
    public void getTotalProjectNum() throws Exception{
        when(projectResourceKpiSnapshotService.getTotalProjectNum()).thenReturn(11);
        mockMvc.perform(get("/dashboard/platform/project_num"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data").isNumber());
    }

    @Test
    public void getPlatformTotalDataTest() throws Exception{
        when(hClient.getTotalDataNum(any())).thenReturn(121211L);
        mockMvc.perform(get("/dashboard/platform/total_data"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data").isNumber());
    }

    @Test
    public void getPlatformDataDailyIncrTest() throws Exception{
        when(projectResourceKpiSnapshotService.getPlatformDataDailyIncr()).thenReturn(33L);
        mockMvc.perform(get("/dashboard/platform/data_daily_incr"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data").isNumber());
    }

    @Test
    public void getPlatformTaskSuccessRateTest() throws Exception{
        when(projectResourceKpiSnapshotService.getTaskSuccessRate()).thenReturn("98.21");
        mockMvc.perform(get("/dashboard/platform/task_success_rate"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data").isString());
    }

    @Test
    public void getResourceTrendTest() throws Exception{
        List<PlatformResourceSnapshot> list = new ArrayList<>();
        PlatformResourceSnapshot p1 = new PlatformResourceSnapshot();
        p1.setAppliedCpu(14);
        p1.setAppliedMemory(50);
        p1.setAppliedStorage(300L);
        p1.setPlatformCpu(1000);
        p1.setPlatformMemory(1000);
        p1.setPlatformStorage(10000L);
        p1.setSnapshotDate(DateUtils.parse("2017-07-19", "yyyy-MM-dd"));
        list.add(p1);
        PlatformResourceSnapshot p2 = new PlatformResourceSnapshot();
        p2.setAppliedCpu(20);
        p2.setAppliedMemory(100);
        p2.setAppliedStorage(500L);
        p2.setPlatformCpu(2000);
        p2.setPlatformMemory(2000);
        p2.setPlatformStorage(20000L);
        p2.setSnapshotDate(DateUtils.parse("2017-07-20", "yyyy-MM-dd"));
        list.add(p2);

        when(platformResourceSnapshotService.getUseRateTrend()).thenReturn(list);
        mockMvc.perform(get("/dashboard/platform/resource/trend"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    public void getDataDailyTrendTest() throws Exception{
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map1 = new HashMap<>();
        map1.put("kpiDate", DateUtils.parse("2017-07-19","yyyy-MM-dd"));
        map1.put("dataDailyIncrTotal", 5);
        list.add(map1);
        Map<String, Object> map2 = new HashMap<>();
        map2.put("kpiDate", DateUtils.parse("2017-07-20","yyyy-MM-dd"));
        map2.put("dataDailyIncrTotal",3);
        list.add(map2);
        when(projectResourceKpiSnapshotService.getTrend()).thenReturn(list);
        mockMvc.perform(get("/dashboard/platform/data_daily/trend"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    public void getTaskSuccessRateTrendTest() throws Exception{
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map1 = new HashMap<>();
        map1.put("kpiDate", DateUtils.parse("2017-07-19","yyyy-MM-dd"));
        map1.put("taskSuccess", 176);
        map1.put("taskTotal",200);
        list.add(map1);
        Map<String, Object> map2 = new HashMap<>();
        map2.put("kpiDate", DateUtils.parse("2017-07-20","yyyy-MM-dd"));
        map2.put("taskSuccess", 213);
        map2.put("taskTotal",233);
        list.add(map2);
        when(projectResourceKpiSnapshotService.getTrend()).thenReturn(list);
        mockMvc.perform(get("/dashboard/platform/task_success_rate/trend"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data").isArray());
    }
}
