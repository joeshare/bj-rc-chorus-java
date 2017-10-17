package cn.rongcapital.chorus.modules.processor.collect_job_info.dao;

import cn.rongcapital.chorus.modules.processor.collect_job_info.entity.ChorusProject;
import org.apache.ibatis.annotations.Select;

/**
 * Created by abiton on 11/08/2017.
 */
public interface ChorusProjectRepository {
    @Select({"select project_id as projectId,project_code as projectCode,project_name as projectName from project_info"
            ," where project_id = #{projectId}"})
    ChorusProject findByProjectId(long projectId);

}
