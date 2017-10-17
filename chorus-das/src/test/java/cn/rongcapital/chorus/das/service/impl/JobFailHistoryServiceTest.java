package cn.rongcapital.chorus.das.service.impl;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import cn.rongcapital.chorus.das.entity.XDExecution;
import cn.rongcapital.chorus.das.service.JobFailHistoryService;
import cn.rongcapital.chorus.das.util.BasicSpringTest;

public class JobFailHistoryServiceTest extends BasicSpringTest {

    @Autowired
    private JobFailHistoryService historyService;

    /**
     * 
     * @author yunzhong
     * @version
     * @since 2017年5月22日
     */
    @Test
    public void testInsert() {
        XDExecution xdExecution = new XDExecution();
        xdExecution.setEndTime(new Date());
        xdExecution.setJobName("testjobName");
        xdExecution.setStartTime(new Date());
        xdExecution.setExecutionId(2);
        xdExecution.setStatus("fail");
        int count = historyService.insert(xdExecution);
        assertTrue(count == 1);
    }

    /**
     * 
     * @author yunzhong
     * @version
     * @since 2017年5月22日
     */
    @Test
    public void testSelectExecutionIds() {
        List<XDExecution> executions = new ArrayList<XDExecution>();
        XDExecution e1 = new XDExecution();
        e1.setExecutionId(1);
        executions.add(e1);
        XDExecution e2 = new XDExecution();
        e2.setExecutionId(0);
        executions.add(e2);

        XDExecution e3 = new XDExecution();
        e3.setExecutionId(100033);
        executions.add(e3);
        Set<String> ids = historyService.selectExecutionIds(executions);

        assertNotNull(ids);
        assertEquals(2, ids.size());
        assertTrue(ids.contains(String.valueOf(0)));
        assertTrue(ids.contains(String.valueOf(1)));
        assertTrue(!ids.contains(String.valueOf(100033)));
    }
}
