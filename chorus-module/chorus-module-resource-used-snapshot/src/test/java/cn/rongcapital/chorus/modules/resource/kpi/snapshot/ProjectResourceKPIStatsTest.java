package cn.rongcapital.chorus.modules.resource.kpi.snapshot;

import cn.rongcapital.chorus.modules.resource.kpi.snapshot.bean.ProjectResourceKPISnapshot;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by hhlfl on 2017-7-18.
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class,ProjectResourceKPIStats.class})
public class ProjectResourceKPIStatsTest{
    @Autowired
    private ProjectResourceKPIStats projectResourceKPIStats;

    @Test
    public void snapshot(){
        try {
            projectResourceKPIStats.snapshot();
            Assert.assertTrue(true);
        } catch (Exception e) {
            log.error(e.getMessage());
            Assert.assertTrue(false);
        }
    }

    @Test
    public void queryProjects(){
        try {
            List<Map<String, Object>> list = projectResourceKPIStats.queryProjects();
            if(list.size()>0){
                Map<String, Object> map = list.get(0);
                Assert.assertTrue(map.get("projectId")!=null);
                Assert.assertTrue(map.get("projectCode")!=null);
                Assert.assertTrue(map.get("projectName")!=null);
            }

            Assert.assertTrue(list.size()>=0);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Assert.assertTrue(false);
        }
    }

    @Test
    public void statsCpuAndMemory(){

        try {
            List<ProjectResourceKPISnapshot> list = projectResourceKPIStats.statsCpuAndMemory();
            if(list.size()>0){
                ProjectResourceKPISnapshot usage = list.get(0);
                Assert.assertTrue(usage.getProjectId()>0);
                Assert.assertEquals(usage.getCpuUsage(),NumberUtils.divide(usage.getCpuUsed(),usage.getCpuTotal(),Constant.precision),0);
                Assert.assertEquals(usage.getMemoryUsage(),NumberUtils.divide(usage.getMemoryUsed(),usage.getMemoryTotal(),Constant.precision),0);
            }

            Assert.assertTrue(list.size()>=0);

        } catch (Exception e) {
            log.error(e.getMessage());
            Assert.assertTrue(false);
        }

    }

    @Test
    public void saveSnapshot(){
        List<ProjectResourceKPISnapshot> projectResourceKPI = new ArrayList<>();
        try {
            ProjectResourceKPISnapshot apTest01 = getProjectResourceUsage(0, "p_test_01");
            projectResourceKPI.add(apTest01);

            projectResourceKPIStats.saveSnapshot(projectResourceKPI);
            Map<Long, ProjectResourceKPISnapshot> qProjectResourceKPIS = projectResourceKPIStats.querySnapshot(new Date());
            Assert.assertTrue(qProjectResourceKPIS.containsKey(0l));
//            Assert.assertTrue(qProjectResourceKPIS.containsKey(2l));

            ProjectResourceKPISnapshot pTest01 = qProjectResourceKPIS.get(0l);
            Assert.assertEquals(apTest01.getMemoryUsage(),pTest01.getMemoryUsage(),0);
            Assert.assertEquals(apTest01.getMemoryTotal(),pTest01.getMemoryTotal(),0);
            Assert.assertEquals(apTest01.getMemoryUsed(),pTest01.getMemoryUsed(),0);
            Assert.assertEquals(apTest01.getCpuUsage(),pTest01.getCpuUsage(),0);
            Assert.assertEquals(apTest01.getCpuTotal(),pTest01.getCpuTotal(),0);
            Assert.assertEquals(apTest01.getCpuUsed(),pTest01.getCpuUsed(),0);
            Assert.assertEquals(apTest01.getStorageUsage(),pTest01.getStorageUsage(),0);
            Assert.assertEquals(apTest01.getStorageTotal(),pTest01.getStorageTotal(),0);
            Assert.assertEquals(apTest01.getStorageUsed(),pTest01.getStorageUsed(),0);
            Assert.assertEquals(apTest01.getTaskTotal(),pTest01.getTaskTotal(),0);
            Assert.assertEquals(apTest01.getTaskSuccess(),pTest01.getTaskSuccess(),0);
            Assert.assertEquals(apTest01.getTaskSuccessRate(),pTest01.getTaskSuccessRate(),0);
            Assert.assertEquals(apTest01.getDataDailyIncr(),pTest01.getDataDailyIncr(),0);
            Assert.assertEquals(apTest01.getKpiDate(),pTest01.getKpiDate());
            Assert.assertEquals(apTest01.getCreateTime(),pTest01.getCreateTime());
            Assert.assertEquals(apTest01.getScore(),pTest01.getScore());
            Assert.assertEquals(apTest01.getProjectId(),pTest01.getProjectId());
            Assert.assertEquals(apTest01.getProjectName(),pTest01.getProjectName());

        } catch (Exception e) {
            log.error(e.getMessage());
            Assert.assertTrue(false);
        }
    }

    @Test
    public void queryJobs(){
        try {
            Map<Long, List<String>> jobs = projectResourceKPIStats.queryJobs(new Date());
            Assert.assertTrue(true);
        } catch (Exception e) {
            log.error(e.getMessage());
            Assert.assertTrue(false);
        }
    }

    @Test
    public void statsJobSuccessRato(){
        try {
            Map<String, int[]> stringMap = projectResourceKPIStats.statsJobSuccessRato(new Date());
            Assert.assertTrue(true);
        } catch (Exception e) {
           log.error(e.getMessage());
           Assert.assertTrue(false);
        }
    }

    @Test
    public void statsStorage(){
        Set<String> projectCodes = new HashSet<>();
        projectCodes.add("mvp02");
        projectCodes.add("llj_1");
        projectCodes.add("hhltest02");
        projectCodes.add("hhltest01");
        try {
            Map<String, Long> stringLongMap = projectResourceKPIStats.statsStorage(projectCodes);
            Assert.assertTrue(true);
        } catch (Exception e) {
            log.error(e.getMessage());
            Assert.assertTrue(false);
        }
    }

    private ProjectResourceKPISnapshot getProjectResourceUsage(long projectId, String projectName) throws ParseException {
        ProjectResourceKPISnapshot usage = new ProjectResourceKPISnapshot();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.sql.Date date = new java.sql.Date(System.currentTimeMillis());
        usage.setProjectId(projectId);
        usage.setProjectName(projectName);
        usage.setCpuUsed(1);
        usage.setCpuTotal(3);
        usage.setCpuUsage(0.33);
        usage.setTaskTotal(0);
        usage.setTaskSuccess(0);
        usage.setTaskSuccessRate(100);

        Timestamp cTime = new Timestamp(date.getTime());
        java.sql.Date createTime = new java.sql.Date(cTime.getTime());
        createTime = new java.sql.Date(sdf.parse(sdf.format(createTime)).getTime());
        usage.setCreateTime(new Timestamp(createTime.getTime()));

        sdf.applyPattern("yyy-MM-dd");
        date = new java.sql.Date(sdf.parse(sdf.format(date)).getTime());
        usage.setKpiDate(date);

        usage.setMemoryTotal(2);
        usage.setMemoryUsed(5);
        usage.setMemoryUsage(0.4);
        usage.setStorageTotal(1024l);
        usage.setStorageUsed(512l);
        usage.setStorageUsage(0.5);
        usage.setDataDailyIncr(512);
        return usage;
    }

}
