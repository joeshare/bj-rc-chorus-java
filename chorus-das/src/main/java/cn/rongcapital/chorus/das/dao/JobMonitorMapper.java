package cn.rongcapital.chorus.das.dao;

import java.util.List;

import cn.rongcapital.chorus.das.entity.ProjectInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import cn.rongcapital.chorus.das.entity.Job;
import cn.rongcapital.chorus.das.entity.JobMonitor;

/**
 * @author Li.ZhiWei
 */
public interface JobMonitorMapper {
    /**
     * 根据job表查询出对应的jobMonitor
     * @return
     */
    @Select({"SELECT job_id as jobId,job_name as jobName,job_alias_name as jobAliasName,description as jobDescription,status as jobStatus ",
            "FROM chorus.job"
        })
    List<JobMonitor> selectAll();
    /**
     * 根据指定job id 查询出与别名相对应的job并装换为job monitor
     * @param jobAliasName
     * @param jobIds
     * @return
     */
    @Select({"<script>",
        "SELECT job_id as jobId,job_name as jobName,job_alias_name as jobAliasName,description as jobDescription,status as jobStatus ",
        "FROM job ",
        "WHERE job_alias_name LIKE CONCAT ('%', #{jobAliasName}, '%') ",
        "AND job.job_id IN ",
        "<foreach item='item' index='index' collection='jobIds'",
        "open='(' separator=',' close=')'>",
        "#{item.jobId}",
        "</foreach>",
        "AND job.project_id IN ",
        "<foreach item='projectId' index='index' collection='projectIds'",
        "open='(' separator=',' close=')'>",
        "#{projectId}",
        "</foreach>",
    "</script>"
    })
    List<JobMonitor> selectAllWithAliasNameAndJob(@Param("jobAliasName") String jobAliasName,@Param("jobIds")List<Job> jobIds,@Param("projectIds") Long[] projectIds);
    /**
     * 根据job id 查询出对应的job 并转为 job monitor
     * @param jobIds
     * @return
     */
    @Select({"<script>",
        "SELECT job_id as jobId,job_name as jobName,job_alias_name as jobAliasName,description as jobDescription,status as jobStatus ",
        "FROM job ",
        "WHERE job.job_id IN ",
        "<foreach item='item' index='index' collection='jobIds'",
        "open='(' separator=',' close=')'>",
        "#{item.jobId}",
        "</foreach>",
    "</script>"
    })
    List<JobMonitor> selectAllWithJobId(@Param("jobIds") List<Job> jobIds);
    /**
     * 根据job id 查询出对应的job 过滤项目 并转为 job monitor
     * @param jobIds
     * @return
     */
    @Select({"<script>",
        "SELECT job_id as jobId,job_name as jobName,job_alias_name as jobAliasName,description as jobDescription,status as jobStatus ",
        "FROM job ",
        "WHERE job.job_id IN ",
        "<foreach item='item' index='index' collection='jobIds'",
        "open='(' separator=',' close=')'>",
        "#{item.jobId}",
        "</foreach>",
        "AND job.project_id IN ",
        "<foreach item='projectId' index='index' collection='projectIds'",
        "open='(' separator=',' close=')'>",
        "#{projectId}",
        "</foreach>",
    "</script>"
    })
    List<JobMonitor> selectAllWithJob(@Param("jobIds") List<Job> jobIds,@Param("projectIds") Long[] projectIds);
    /**
     * 根据用户和项目名修改项目被过滤状态
     * @param
     */
    @Update({
        "<script>",
        "UPDATE project_member_mapping pmm SET pmm.is_filtered=#{state} ",
        "WHERE pmm.project_id IN ",
        "<foreach item='projectId' index='index' collection='projectIds'",
        "open='(' separator=',' close=')'>",
        "#{projectId}",
        "</foreach>",
        "AND pmm.user_id=#{user.subId} ",
        "</script>",
    })
    int modifyProjectFilterState(@Param("projectIds") Long[] projectIds,@Param("state") String state,@Param("userId") String userId);

    /**
     * 查询与指定用户相关联的项目
     * @param userId
     * @return 项目集合对象
     */
    @Select({
            "SELECT project_id as projectId, project_code as projectCode,project_name as projectName,project_desc as projectDesc,project_manager_id as projectManagerId ,manager_telephone as managerTelephone ,create_user_id as createUserId , create_time as createTime ,update_user_id as updateUserId , update_time as updateTime ",
            "FROM project_info p WHERE p.project_id ",
            "IN ",
            "(SELECT DISTINCT (pmm.project_id) FROM project_member_mapping pmm WHERE pmm.user_id=#{userId} )"
    })
    List<ProjectInfo> getProjectRelatedWithUser(String userId);
}
