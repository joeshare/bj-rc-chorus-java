package cn.rongcapital.chorus.modules.processor.collect_job_info.dao;

import cn.rongcapital.chorus.modules.processor.collect_job_info.entity.ChorusTask;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by abiton on 10/08/2017.
 */
public interface ChorusTaskRepository {
    @Select({"select job_id as jobId,task_id as taskId,module_name as moduleName,task_name as taskName,alias_name as aliasName from task"
    ," where job_id = #{jobId} "})
    List<ChorusTask> findByJobId(int jobId);
    @Select({"select job_id as jobId,task_id as taskId,module_name as moduleName,task_name as taskName,alias_name as aliasName from task"
            ," where task_name = #{taskName} "})
    ChorusTask findByTaskName(String taskName);
}
