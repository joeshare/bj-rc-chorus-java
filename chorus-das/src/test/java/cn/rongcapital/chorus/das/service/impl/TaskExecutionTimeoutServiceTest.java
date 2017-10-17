package cn.rongcapital.chorus.das.service.impl;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import cn.rongcapital.chorus.das.entity.TaskExecutionTimeout;
import cn.rongcapital.chorus.das.entity.XDExecution;
import cn.rongcapital.chorus.das.service.TaskExecutionService;
import cn.rongcapital.chorus.das.util.BasicSpringTest;

public class TaskExecutionTimeoutServiceTest extends BasicSpringTest {
    @Autowired
    private TaskExecutionService executionService;

    /**
     * 
     * @author yunzhong
     * @time 2017年6月27日下午5:43:41
     */
    @Test
    @Transactional(transactionManager = "txManagerChorus")
    @Rollback(true)
    public void testinsert() {
        TaskExecutionTimeout timeout = new TaskExecutionTimeout();
        timeout.setTaskExecutionId(2234);
        timeout.setTaskId(11);
        timeout.setStartTime(new Date());
        timeout.setExpectEndTime(new Date());
        int insert = executionService.insert(timeout);
        assertTrue(insert > 0);
    }

    /**
     * 
     * @author yunzhong
     * @time 2017年6月27日下午6:00:26
     */
    @Test
    public void test() {
        List<XDExecution> xdExecutions = new ArrayList<>();
        XDExecution exExe = new XDExecution();
        exExe.setExecutionId(1);
        xdExecutions.add(exExe);

        XDExecution exExe2 = new XDExecution();
        exExe2.setExecutionId(2);
        xdExecutions.add(exExe2);
        Set<Long> filterNotified = executionService.filterNotified(xdExecutions);
        assertNotNull(filterNotified);
        assertTrue(filterNotified.contains(1L));
        assertTrue(filterNotified.contains(2L));
    }
}
