package cn.rongcapital.chorus.server.dashboard.controller;

import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.das.entity.ProjectResourceKpiSnapshotVO;
import cn.rongcapital.chorus.das.service.ProjectResourceKpiSnapshotService;
import com.github.pagehelper.Page;
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

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by hhlfl on 2017-7-21.
 */
@RunWith(PowerMockRunner.class)
@PowerMockIgnore("javax.management.*")
public class ProjectResourceKpiControllerTest {
    private MockMvc mockMvc;
    @Mock
    private ProjectResourceKpiSnapshotService projectResourceKpiSnapshotService;
    @InjectMocks
    private ProjectResourceKpiController projectResourceKpiController;
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(projectResourceKpiController).build();
    }

    @Test
    public void queryYesterdayProjectResourceKpi() throws Exception {
        int pageNum=1;
        int pageSize=5;
        long store = 1024102410241024l;
        ProjectResourceKpiSnapshotVO kpiSnapshot1 = new ProjectResourceKpiSnapshotVO();
        kpiSnapshot1.setCpuUsage(0.5);
        kpiSnapshot1.setMemoryUsage(0.4);
        kpiSnapshot1.setStorageUsage(0.5);
        kpiSnapshot1.setStorageUsed(store);
        kpiSnapshot1.setDataDailyIncr(2014l);
        kpiSnapshot1.setTaskSuccessRate(0.33);
        kpiSnapshot1.setUserName("aa");

        Page<ProjectResourceKpiSnapshotVO> page = new Page(pageNum,pageSize);
        page.add(kpiSnapshot1);
        when(projectResourceKpiSnapshotService.selectByKpiDate(anyObject(),eq(pageNum),eq(pageSize))).thenReturn(page);

        mockMvc.perform(get("/dashboard/platform/project/kpi/" + pageNum + "/" + pageSize ))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.startRow").value((pageNum-1)*pageSize+1))
                .andExpect(jsonPath("$.data.endRow").value((pageNum-1)*pageSize+(1)))
                .andExpect(jsonPath("$.data.list").isArray())
                .andExpect(jsonPath("$.data.list[0].cpuUsage").value(50.0))
                .andExpect(jsonPath("$.data.list[0].memoryUsage").value(40.0))
                .andExpect(jsonPath("$.data.list[0].storageUsage").value(50.0))
                .andExpect(jsonPath("$.data.list[0].storageUsed").value(store))
                .andExpect(jsonPath("$.data.list[0].dataDailyIncr").value(2014))
                .andExpect(jsonPath("$.data.list[0].taskSuccessRate").value(33.0))
        ;


        pageNum=2;
        pageSize=5;
        when(projectResourceKpiSnapshotService.selectByKpiDate(anyObject(),eq(pageNum),eq(pageSize))).thenThrow(Exception.class);


        mockMvc.perform(get("/dashboard/platform/project/kpi/" + pageNum + "/" + pageSize ))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.code").value(StatusCode.SYSTEM_ERR.getCode()))
        ;

    }

    @Test
    public void queryYesterdayProjectResourceKpiWithOrder() throws Exception {
        int pageNum=1;
        int pageSize=5;
        long store = 1024102410241024l;
        ProjectResourceKpiSnapshotVO kpiSnapshot1 = new ProjectResourceKpiSnapshotVO();
        kpiSnapshot1.setCpuUsage(0.5);
        kpiSnapshot1.setMemoryUsage(0.4);
        kpiSnapshot1.setStorageUsage(0.5);
        kpiSnapshot1.setStorageUsed(store);
        kpiSnapshot1.setDataDailyIncr(2014l);
        kpiSnapshot1.setTaskSuccessRate(0.33);
        kpiSnapshot1.setUserName("aa");

        Page<ProjectResourceKpiSnapshotVO> page = new Page(pageNum,pageSize);
        page.add(kpiSnapshot1);
        when(projectResourceKpiSnapshotService.selectByKpiDate(anyObject(),eq(pageNum),eq(pageSize))).thenReturn(page);
        when(projectResourceKpiSnapshotService.selectByKpiDateWithOrder(anyObject(),eq(pageNum),eq(pageSize))).thenReturn(page);
        for (int i = 0;i <=7;i++) {
            mockMvc.perform(get("/dashboard/platform/project/kpi/" + pageNum + "/" + pageSize + "/" + i + "/" + 0))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                    .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS.getCode()))
                    .andExpect(jsonPath("$.data.startRow").value((pageNum-1)*pageSize+1))
                    .andExpect(jsonPath("$.data.endRow").value((pageNum-1)*pageSize+(1)))
                    .andExpect(jsonPath("$.data.list").isArray())
                    .andExpect(jsonPath("$.data.list[0].cpuUsage").value(50.0))
                    .andExpect(jsonPath("$.data.list[0].memoryUsage").value(40.0))
                    .andExpect(jsonPath("$.data.list[0].storageUsage").value(50.0))
                    .andExpect(jsonPath("$.data.list[0].storageUsed").value(store))
                    .andExpect(jsonPath("$.data.list[0].dataDailyIncr").value(2014))
                    .andExpect(jsonPath("$.data.list[0].taskSuccessRate").value(33.0))
            ;
        }

        pageNum=2;
        pageSize=5;
        when(projectResourceKpiSnapshotService.selectByKpiDateWithOrder(anyObject(),eq(pageNum),eq(pageSize))).thenThrow(Exception.class);


        mockMvc.perform(get("/dashboard/platform/project/kpi/" + pageNum + "/" + pageSize + "/" + 1 + "/" + 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.code").value(StatusCode.SYSTEM_ERR.getCode()))
        ;

    }
}