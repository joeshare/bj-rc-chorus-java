import cn.rongcapital.chorus.modules.ranger.audit.stats.RangerAuditStatsTask;
import cn.rongcapital.chorus.modules.ranger.audit.stats.bean.ResourceUsedStats;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by hhlfl on 2017-6-28.
 */
public class RangerAuditStatsTaskTest{
    private RangerAuditStatsTask task;

    @Before
    public void setUp(){
        String url="jdbc:mysql://10.200.48.79:3306/chorus?useUnicode=true&characterEncoding=utf-8&useSSL=false";
        String userName="dps";
        String password="Dps@10.200.48.MySQL";
        String zkHost="dl-rc-optd-ambari-slave-v-test-1.host.dataengine.com:2181,dl-rc-optd-ambari-slave-v-test-2.host.dataengine.com:2181,dl-rc-optd-ambari-slave-v-test-3.host.dataengine.com:2181/ranger_audits";
        String collection="ranger_audits";
        task = new RangerAuditStatsTask(url,userName,password,zkHost,collection);
    }

    @Test
    public void doWork(){
        try {
            task.doWork(7, "chorus_test_hive");
            Assert.assertTrue(true);
        }catch (Exception ex){
            Assert.assertTrue(false);
        }
    }

    @Test
    public void batchInsertWitchOverride(){
        List<ResourceUsedStats> records = new ArrayList<ResourceUsedStats>();
        ResourceUsedStats usedStats = new ResourceUsedStats();
        usedStats.setUpdateTime(new Date());
        usedStats.setUsedCount(1l);
        usedStats.setResourceType("table");
        usedStats.setServiceName("chorus_test_hive");
        usedStats.setResourceName("t_hhltest01");
        usedStats.setPolicyId("policy_001");
        records.add(usedStats);
        try {
            task.batchInsertWitchOverride(records);
            Assert.assertTrue(true);
        }catch (Exception ex){
            Assert.assertTrue(false);
        }
    }


}
