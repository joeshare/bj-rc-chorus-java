package cn.rongcapital.chorus.modules.processor.collect_job_info.dao;

import cn.rongcapital.chorus.modules.processor.collect_job_info.entity.ChorusJob;

import org.apache.ibatis.annotations.Select;
/**
 * Created by abiton on 10/08/2017.
 */
public interface ChorusJobRepository {
    @Select({"select job_id as jobId,job_name as jobName,project_id as projectId,job_alias_name as jobAliasName,create_user_name as userName " +
            "from job ","where job_name = #{jobName}"})
    ChorusJob findByJobName(String jobName);

    @Select({"select job_id as jobId,job_name as jobName,project_id as projectId,job_alias_name as jobAliasName,create_user_name as userName " +
            "from job ","where job_id = #{jobId}"})
    ChorusJob findByJobId(int jobId);

}
