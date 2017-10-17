package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.common.util.NumUtils;
import cn.rongcapital.chorus.das.dao.ProjectResourceKpiSnapshotMapper;
import cn.rongcapital.chorus.das.dao.ResourceInnerMapper;
import cn.rongcapital.chorus.das.entity.XDTaskNum;
import cn.rongcapital.chorus.das.service.impl.ProjectResourceKpiSnapshotServiceImpl;
import cn.rongcapital.chorus.das.xd.dao.XDMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Created by Athletics on 2017/7/19.
 */

public class ProjectResourceKpiSnapshotServiceImplTest {
    @Mock
    private ResourceInnerMapper resourceInnerMapper;
    @Mock
    private ProjectResourceKpiSnapshotMapper projectResourceKpiSnapshotMapper;
    @Mock
    private XDMapper xdMapper;
    @InjectMocks
    private ProjectResourceKpiSnapshotServiceImpl snapshotService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void queryUseRateTest(){
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map1 = new HashMap<>();
        map1.put("cpu", new BigDecimal("5"));
        map1.put("memory", new BigDecimal("10"));
        map1.put("storage", new BigDecimal("100"));
        list.add(map1);
        when(resourceInnerMapper.queryUseRate()).thenReturn(map1);
        Map<String, Long> map = snapshotService.queryUseRate();
        System.out.println(NumUtils.percent(map.get("cpu"),100L));
        System.out.println(NumUtils.percent(map.get("memory"), 100L));
        System.out.println(NumUtils.percent(map.get("storage"), 1000L));
        assertNotNull(map);
        assertEquals(5L, map.get("cpu").longValue());
        assertEquals(10L, map.get("memory").longValue());
        assertEquals(100L,map.get("storage").longValue());
    }

    @Test
    public void getTotalProjectNumTest(){
        when(resourceInnerMapper.queryCountNum()).thenReturn(1);
        Integer result = snapshotService.getTotalProjectNum();
        assertTrue(result == 1);
    }

    @Test
    public void getPlatformDataDailyIncrTest(){
        when(projectResourceKpiSnapshotMapper.queryDataDailyIncrTotalNumByDate(any())).thenReturn(1L);
        Long result = snapshotService.getPlatformDataDailyIncr();
        assertTrue(result==1L);
    }

    @Test
    public void getTaskSuccessRateTest(){
        XDTaskNum taskNum = new XDTaskNum();
        taskNum.setSuccessCount(21);
        taskNum.setTotal(25);
        when(xdMapper.getPlatformTaskSuccessNum()).thenReturn(taskNum);
        String result = snapshotService.getTaskSuccessRate();
        assertTrue("84".equals(result));
    }

    @Test
    public void getTrendTest(){
        List<Map<String ,Object>> list = new ArrayList<>();
        Map<String, Object> map1 = new HashMap<>();
        map1.put("kpiDate","2017-07-19");
        map1.put("dataDailyIncrTotal",20);
        map1.put("taskTotal", 25);
        map1.put("taskSuccess", 50);
        list.add(map1);
        Map<String, Object> map2 = new HashMap<>();
        map2.put("kpiDate", "2017-07-20");
        map2.put("dataDailyIncrTotal",25);
        map2.put("taskTotal", 29);
        map2.put("taskSuccess", 55);
        list.add(map2);
        when(projectResourceKpiSnapshotMapper.getTrend(any())).thenReturn(list);
        List<Map<String, Object>> result = snapshotService.getTrend();
        assertNotNull(result);
    }
}
