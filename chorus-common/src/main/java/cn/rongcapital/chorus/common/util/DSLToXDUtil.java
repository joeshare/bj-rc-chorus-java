package cn.rongcapital.chorus.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.rongcapital.chorus.common.dsl.xd.model.DSLTask;
import cn.rongcapital.chorus.common.dsl.xd.model.TaskDefinition;
import cn.rongcapital.chorus.common.dsl.xd.model.TaskModel;
import cn.rongcapital.chorus.common.dsl.xd.model.WorkFlowForXDGTree;

public class DSLToXDUtil {
    public static final String[] TASK_PARAM = {"name","taskReferenceName","type","inputParameters","workerType","workerParams"};
    /**
     * 批量任务Workflow DSL to XD
     * @param jsonWorkflow
     */
    public static String batchWorkflowDslToXd(String jsonWorkflow) throws Exception{
        WorkFlowForXDGTree xdgTree = new WorkFlowForXDGTree(jsonWorkflow);
        String dslToXD = xdgTree.parseDSLToXD();

        return dslToXD;
    }
    /**
     * 实时任务Workflow DSL to XD
     * @param jsonObj
     * @throws Exception
     */
    public static String streamTaskDslToXd(String jsonWorkflow, String taskListJson) throws Exception{
        List<TaskModel> taskModels = new ArrayList<TaskModel>();
        taskModels = JsonUtils.array2List(taskListJson,TaskModel.class);
        WorkFlowForXDGTree xdgTree = new WorkFlowForXDGTree(jsonWorkflow);
        String streamDSLToXD = xdgTree.parseStreamDSLToXD(taskModels);

        return streamDSLToXD;
    }
    /**
     * 任务定义 DSL to XD
     * @param jsonTask
     * @return
     * @throws Exception
     */
    public static String taskDslDefinitionToXd(String moudleName ,String jsonTask) throws Exception{
        TaskDefinition task = JsonUtils.Json2Object(jsonTask,TaskDefinition.class);
        Map<String, String> staticParams = task.getStaticParams();
        StringBuilder taskBuilder = new StringBuilder();
        taskBuilder.append(moudleName);

        for (Map.Entry<String, String> entry : staticParams.entrySet())
        {
            String param;
            if("args".equals(entry.getKey())){
                param = " --"+entry.getKey()+"=\""+entry.getValue()+"\"";
            }
            else{
                param = " --"+entry.getKey()+"="+entry.getValue();
            }
            taskBuilder.append(param);
        }

        return taskBuilder.toString();
    }

    public static String taskObjToJson(DSLTask task) throws Exception{
        String taskJson = JsonUtils.toJSONObject(task,TASK_PARAM).toString();
        return taskJson;
    }

}
