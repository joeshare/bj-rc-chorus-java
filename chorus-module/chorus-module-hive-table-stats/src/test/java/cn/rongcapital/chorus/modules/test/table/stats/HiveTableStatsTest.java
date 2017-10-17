package cn.rongcapital.chorus.modules.test.table.stats;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.rongcapital.chorus.modules.table.stats.HiveTableStats;
import cn.rongcapital.chorus.modules.table.stats.bean.HiveTableMonitorData;
import cn.rongcapital.chorus.modules.test.table.stats.util.TestConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
@Configuration
public class HiveTableStatsTest {

    @Autowired
    private HiveTableStats hiveTableStats;

    /**
     * 
     * @author yunzhong
     * @time 2017年8月9日下午2:33:52
     */
    @Test
    public void testlistAllTables() {
        log.info("Start test.");
        hiveTableStats.setCurrentDate(new Date());
        List<HiveTableMonitorData> listAllTables = null;
        try {
            final Set<Long> listProjects = hiveTableStats.listProjects();
            listAllTables = hiveTableStats.listAllTables(1, 100, listProjects);
        } catch (Exception e) {
            log.error("", e);
            fail(e.getLocalizedMessage());
        }
        assertNotNull(listAllTables);
        for (HiveTableMonitorData data : listAllTables) {
            assertNotNull(data.getMonitorDate());
            assertNotNull(data.getProjectCode());
            assertNotNull(data.getProjectId());
            assertNotNull(data.getTableInfoId());
            assertNotNull(data.getTableName());
        }
        log.info("end test.");
    }

    /**
     * 
     * @author yunzhong
     * @time 2017年8月9日下午2:33:49
     */
    @Test
    public void testlistAllTablesEmpty() {
        log.info("Start test.");
        hiveTableStats.setCurrentDate(new Date());
        List<HiveTableMonitorData> listAllTables = null;
        try {
            final Set<Long> listProjects = hiveTableStats.listProjects();
            listAllTables = hiveTableStats.listAllTables(20, 100, listProjects);
        } catch (Exception e) {
            log.error("", e);
            fail(e.getLocalizedMessage());
        }
        assertNotNull(listAllTables);
        assertTrue(listAllTables.isEmpty());
        log.info("end test.");
    }

    /**
     * 
     * @author yunzhong
     * @time 2017年8月9日下午2:46:16
     */
    @Test
    public void testassembleRow() {
        HiveTableMonitorData table = new HiveTableMonitorData();
        table.setMonitorDate(new Date());
        table.setProjectCode("yunProject11");
        table.setProjectId(222675L);
        table.setTableInfoId("");
        table.setTableName("yunzhongtest");
        try {
            hiveTableStats.assembleRow(table, hiveTableStats.generateHiveDBName("yunProject11"), table.getTableName());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getLocalizedMessage());
        }
        assertTrue(table.getRows() > 0);
    }

    /**
     * 
     * @author yunzhong
     * @time 2017年8月9日下午2:47:30
     */
    @Test
    public void testAssembleStorage() {
        HiveTableMonitorData table = new HiveTableMonitorData();
        table.setMonitorDate(new Date());
        table.setProjectCode("yunProject11");
        table.setProjectId(222675L);
        table.setTableInfoId("");
        table.setTableName("undelete1");
        hiveTableStats.assembleStorage(table, table.getProjectCode(), table.getTableName());
        assertTrue(table.getStorageSize() > 0);
    }

    /**
     * 
     * @author yunzhong
     * @time 2017年8月9日下午3:14:29
     */
    @Test
    public void testAssembleStorageNotExist() {
        HiveTableMonitorData table = new HiveTableMonitorData();
        table.setMonitorDate(new Date());
        table.setProjectCode("yunProject11");
        table.setProjectId(222675L);
        table.setTableInfoId("");
        table.setTableName("yunzhongtest11");
        hiveTableStats.assembleStorage(table, table.getProjectCode(), table.getTableName());
        assertTrue(table.getStorageSize() == 0);
    }

    /**
     * 
     * @author yunzhong
     * @time 2017年8月9日下午3:16:07
     */
    @Test
    public void testBatchInsert() {
        Random random = new Random();
        try {
            hiveTableStats.setCurrentDate(new Date());
            final Set<Long> listProjects = hiveTableStats.listProjects();
            List<HiveTableMonitorData> tableData = hiveTableStats.listAllTables(1, 100, listProjects);
            tableData.forEach((table) -> {
                table.setStorageSize(random.nextInt(100));
                table.setRows(random.nextInt(10));
            });
            hiveTableStats.batchInsert(tableData);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getLocalizedMessage());
        }
    }

    /**
     * 
     * @author yunzhong
     * @time 2017年8月31日上午11:46:19
     */
    @Test
    public void testListProject() {
        Set<Long> listProjects = null;
        try {
            listProjects = hiveTableStats.listProjects();
        } catch (SQLException e) {
            e.printStackTrace();
            fail(e.getLocalizedMessage());
        }
        assertNotNull(listProjects);
    }

    @Test
    public void teststatistics() {
        hiveTableStats.setCurrentDate(new Date());
        try {
            hiveTableStats.statistics();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getLocalizedMessage());
        }
    }
}
