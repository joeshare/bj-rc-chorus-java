package cn.rongcapital.chorus.common.util;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class TestDSLToXDUtil {

    @Test
    public void batchWorkflowDslToXdForkTest() throws Exception{
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("dsl-batchjob-fork-json").getFile());
        String jsonFile = FileUtils.readFileToString(file);

        String expectedDSL = "< mytaskA || < mytaskB & mytaskC > & mytaskD & mytaskF > || mytaskW ";
        String workflowDslToXd = DSLToXDUtil.batchWorkflowDslToXd(jsonFile);

        assertEquals(expectedDSL, workflowDslToXd);
    }

    @Test
    public void batchWorkflowDslToXdForkStartWithTaskTest() throws Exception{
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("dsl-batchjob-fork1-json").getFile());
        String jsonFile = FileUtils.readFileToString(file);

        String expectedDSL = "mytaskX || < mytaskA || < mytaskB & mytaskC > & mytaskD & mytaskF > || mytaskW ";
        String workflowDslToXd = DSLToXDUtil.batchWorkflowDslToXd(jsonFile);

        assertEquals(expectedDSL, workflowDslToXd);
    }

    @Test
    public void batchWorkflowDslToXdForkStartWithForkTest() throws Exception{
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("dsl-batchjob-fork2-json").getFile());
        String jsonFile = FileUtils.readFileToString(file);

        String expectedDSL = "< mytaskA || < mytaskB & mytaskC > & mytaskD & mytaskF > ";
        String workflowDslToXd = DSLToXDUtil.batchWorkflowDslToXd(jsonFile);

        assertEquals(expectedDSL, workflowDslToXd);
    }

    @Test
    public void batchWorkflowDslToXdForkEndWithForkTest() throws Exception{
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("dsl-batchjob-fork3-json").getFile());
        String jsonFile = FileUtils.readFileToString(file);

        String expectedDSL = "mytaskX || < mytaskA || < mytaskB & mytaskC > & mytaskD & mytaskF > ";
        String workflowDslToXd = DSLToXDUtil.batchWorkflowDslToXd(jsonFile);

        assertEquals(expectedDSL, workflowDslToXd);
    }

    @Test
    public void batchWorkflowDslToXdClearForkTest() throws Exception{
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("dsl-batchjob-fork4-json").getFile());
        String jsonFile = FileUtils.readFileToString(file);

        String expectedDSL = "< mytaskA || < mytaskB & mytaskC > & mytaskR & mytaskT & mytaskF > || mytaskW ";
        String workflowDslToXd = DSLToXDUtil.batchWorkflowDslToXd(jsonFile);

        assertEquals(expectedDSL, workflowDslToXd);
    }

    @Test
    public void batchWorkflowDslToXdLinearTest() throws Exception{
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("dsl-batchjob-linear-json").getFile());
        String jsonFile = FileUtils.readFileToString(file);

        String expectedDSL = "mytaskA || mytaskB ";
        String workflowDslToXd = DSLToXDUtil.batchWorkflowDslToXd(jsonFile);

        assertEquals(expectedDSL, workflowDslToXd);
    }

    @Test
    public void batchWorkflowDslToXdLinearThreeTaskTest() throws Exception{
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("dsl-batchjob-linear2-json").getFile());
        String jsonFile = FileUtils.readFileToString(file);

        String expectedDSL = "mytaskA || mytaskB || mytaskC ";
        String workflowDslToXd = DSLToXDUtil.batchWorkflowDslToXd(jsonFile);

        assertEquals(expectedDSL, workflowDslToXd);
    }

    @Test
    public void batchWorkflowDslToXdLinearFourTaskTest() throws Exception{
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("dsl-batchjob-linear3-json").getFile());
        String jsonFile = FileUtils.readFileToString(file);

        String expectedDSL = "mytaskA || mytaskB || mytaskC || mytaskD ";
        String workflowDslToXd = DSLToXDUtil.batchWorkflowDslToXd(jsonFile);

        assertEquals(expectedDSL, workflowDslToXd);
    }

    @Test
    public void batchWorkflowDslToXdLinearSingleTaskTest() throws Exception{
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("dsl-batchjob-linear1-json").getFile());
        String jsonFile = FileUtils.readFileToString(file);

        String expectedDSL = "mytaskA ";
        String workflowDslToXd = DSLToXDUtil.batchWorkflowDslToXd(jsonFile);

        assertEquals(expectedDSL, workflowDslToXd);
    }

    @Test
    public void streamTaskDslToXdTest() throws Exception{
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("dsl-stream-json").getFile());
        File taskFile = new File(classLoader.getResource("dsl-stream-tasklist-json").getFile());
        String jsonFile = FileUtils.readFileToString(file);
        String taskListFile = FileUtils.readFileToString(taskFile);

        String expectedDSL = "chorus_uuid123:file --name=current-name | chorus_uuid333:uppercase | chorus_uuid444:hdfs --fileName=coup --directory=/xd/incake";
        String streamTaskDslToXd = DSLToXDUtil.streamTaskDslToXd(jsonFile,taskListFile);

        assertEquals(expectedDSL, streamTaskDslToXd);
    }

    @Test
    public void taskDslDefinitionToXdTest() throws Exception{
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("dsl-taskdefinition-json").getFile());
        String jsonFile = FileUtils.readFileToString(file);

        String expectedDSL = "RDB2Hive --args=\"--connect 'jdbc:mysql://10.200.48.79:3306/dps?useUnicode=true&characterEncoding=utf-8' --username 'dps' --password 'Dps@10.200.48.MySQL' --table 'tb_user' --hive-import --hive-database 'chorus_lhz' --hive-table 'multicol_table' --hive-overwrite --hive-partition-key 'p_date' --hive-partition-value '$today_yyyyMMdd' --delete-target-dir --columns 'id,status,name' --m 1\" --command=import";
        String taskDslDefinitionToXd = DSLToXDUtil.taskDslDefinitionToXd("RDB2Hive", jsonFile);

        assertEquals(expectedDSL, taskDslDefinitionToXd);
    }

}
