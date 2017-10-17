package cn.rongcapital.chorus.das.service.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import cn.rongcapital.chorus.das.entity.TableMonitorInfo;
import cn.rongcapital.chorus.das.service.TableMonitorService;
import cn.rongcapital.chorus.das.util.BasicSpringTest;

/**
 * @author yunzhong
 *
 */
public class TableMonitorServiceTest extends BasicSpringTest {

    @Autowired
    private TableMonitorService monitorService;

    /**
     * 
     * @author yunzhong
     * @time 2017年6月21日上午10:44:27
     */
    @Test
    @Transactional(transactionManager = "txManagerChorus")
    @Rollback(true)
    public void testInsert() {
        TableMonitorInfo table = new TableMonitorInfo();
        table.setMonitorDate(new Date());
        table.setProjectId(2L);
        table.setTableInfoId(123457126L);
        table.setRows(1301L);
        table.setStorageSize(12244L);
        int count = monitorService.insert(table);
        assertTrue(count == 1);
    }

    /**
     * 
     * @author yunzhong
     * @time 2017年6月21日上午10:50:10
     */
    @Test
    public void testselectRowsTop() {
        Long projectId = 222597L;
        Integer top = 5;
        List<TableMonitorInfo> top5 = monitorService.selectRowsTop(projectId, top);
        assertNotNull(top5);
    }

    /**
     * 
     * @author yunzhong
     * @time 2017年6月21日上午10:55:14
     */
    @Test
    public void testselectStorageTop() {
        Long projectId = 222597L;
        Integer top = 5;
        List<TableMonitorInfo> top5 = monitorService.selectStorageTop(projectId, top);
        assertNotNull(top5);
    }

    /**
     * 
     * @author yunzhong
     * @time 2017年6月21日上午11:11:50
     */
    @Test
    public void testSelectStorageTotal() {
        Long projectId = 222597L;
        Long total = monitorService.selectStorageTotal(projectId);
        assertNotNull(total);
    }
}
