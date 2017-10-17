package cn.rongcapital.chorus.das.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;

import cn.rongcapital.chorus.das.entity.ExecutingJobInfo;
import cn.rongcapital.chorus.das.entity.Job;
import cn.rongcapital.chorus.das.entity.ProjectInfo;
import cn.rongcapital.chorus.das.entity.XDExecution;
import cn.rongcapital.chorus.das.entity.web.JobCause;

public interface JobMapper {

	/**
	 * 添加数据任务
	 *
	 * @param job
	 * @return
	 */
	@Insert({
		"insert into job (job_id, job_type, project_id, job_name, job_alias_name, instance_id, work_flow_dsl, data_input, data_output, description,job_parameters,warning_config,status,use_yn, create_user, create_user_name, create_time, update_user, update_user_name, update_time ) values (" +
		"#{jobId,jdbcType=INTEGER}, ",
		"#{jobType,jdbcType=INTEGER}, ",
		"#{projectId,jdbcType=INTEGER}, ",
		"#{jobName,jdbcType=VARCHAR},",
		"#{jobAliasName,jdbcType=VARCHAR},",
		"#{instanceId,jdbcType=INTEGER}, ",
		"#{workFlowDSL,jdbcType=VARCHAR},",
		"#{dataInput,jdbcType=VARCHAR},",
		"#{dataOutput,jdbcType=VARCHAR},",
		"#{description,jdbcType=VARCHAR}, ",
		"#{jobParameters,jdbcType=VARCHAR},",
		"#{warningConfig,jdbcType=VARCHAR},",
		"#{status,jdbcType=VARCHAR},",
		"#{useYn,jdbcType=VARCHAR},",
		"#{createUser,jdbcType=VARCHAR}, ",
		"#{createUserName,jdbcType=VARCHAR}, ",
		"#{createTime,jdbcType=TIMESTAMP}, ",
		"#{updateUser,jdbcType=VARCHAR}, ",
		"#{updateUserName,jdbcType=VARCHAR}, ",
		"#{updateTime,jdbcType=TIMESTAMP}) "
	})
	@Options(useGeneratedKeys=true, keyProperty="jobId", keyColumn="job_id")
	int addJob(Job job);

	/**
	 * 更新
	 * @param job
	 * @return
	 */
	@Update({
		"update job set job_type=#{jobType},",
		"job_name=#{jobName},",
		"job_alias_name=#{jobAliasName},",
		"instance_id=#{instanceId},",
		"warning_config=#{warningConfig},",
		"work_flow_dsl=#{workFlowDSL},",
		"data_input=#{dataInput},",
		"data_output=#{dataOutput},",
		"description=#{description},",
		"job_parameters=#{jobParameters},",
		"status=#{status},",
		"use_yn=#{useYn},",
		"deploy_user=#{deployUserId},",
		"deploy_user_name=#{deployUserName},",
		"update_user=#{updateUser},",
		"update_user_name=#{updateUserName},",
		"update_time=#{updateTime}",
		"where job_id=#{jobId} "
	})
	int updateJob(Job job);

	/**
	 * 列出所有数据任务
	 *
	 * @return
	 */
	@Select({
		"select job_id as jobId, job_type as jobType, project_id as projectId, job_name as jobName, job_alias_name as jobAliasName, instance_id as instanceId, work_flow_dsl AS workFlowDSL, description,job_parameters as jobParameters,status as status, data_input as dataInput, data_output as dataOutput, use_yn as useYn, create_user as createUser, create_time as createTime,update_user as updateUser,update_time as updateTime ",
		" from job where use_yn = 'Y' "
	})
	List<Job> selectAll();

	/**
	 * 删除数据任务
	 */
	@Delete({ "delete from job where job_id = #{jobId}" })
	int delJob(int jobId);

	/**
	 * 逻辑删除数据任务
	 */
	@Update({
		"update job set status='DELETE',",
		"use_yn='N',",
		"update_user=#{updateUser},",
		"update_user_name=#{updateUserName},",
		"update_time=#{updateTime}",
		"where job_id=#{jobId} "
	})
	int logicDelJob(JobCause job);

	/**
	 * 根据任务ID查询数据任务信息
	 *
	 * @param jobId
	 */
	@Select({
			"select job_id as jobId, job_type as jobType, project_id as projectId, job_name as jobName, job_alias_name as jobAliasName, instance_id as instanceId, work_flow_dsl AS workFlowDSL, description,job_parameters as jobParameters,status as status, data_input as dataInput, data_output as dataOutput, warning_config as warningConfig, deploy_user AS deployUserId, deploy_user_name AS deployUserName, create_user AS createUser,create_user_name AS createUserName, use_yn as useYn, create_time as createTime,update_user as updateUser,update_time as updateTime ",
			"from job ", "where job_id = #{jobId} and use_yn = 'Y'" })
	Job selectJob(int jobId);

	/**
	 * 根据任务ID查询数据任务信息
	 *
	 * @param jobCause
	 */
	@SelectProvider(type = JobSqlProvider.class, method = "getProjectJobList")
	List<Job> getProjectJobList(JobCause jobCause);

	/**
	 * 根据输出表Id查询相关Job<br>
	 *
	 * @param outputTableId 输出表Id
	 */
	@Select({
			"SELECT a.job_id as jobId, a.job_type as jobType, a.project_id as projectId, a.job_name as jobName, job_alias_name as jobAliasName, a.work_flow_dsl AS workFlowDSL, a.description, a.data_input as dataInput, a.data_output as dataOutput, use_yn as useYn, a.create_user as createUser, a.create_time as createTime, a.update_user as updateUser, a.update_time as updateTime ",
			"FROM job a WHERE a.data_output REGEXP #{outputTableId} and a.use_yn = 'Y' ORDER BY a.create_time "
	})
	List<Job> getJobByOutputTable(String outputTableId);

	/**
	 * 更新Job实例ID
	 * @param jobCause
	 * @return
	 */
	@Update({
			"update job set instance_id=#{instanceId},",
			"update_user=#{updateUser},",
			"update_user_name=#{updateUserName},",
			"update_time=#{updateTime}",
			"where job_id=#{jobId} "
	})
    void updateJobInstance(JobCause jobCause);

	/**
	 * 查询与指定项目相关的所有任务
	 * @param project
	 * @return
	 */
	@Select({
	    "SELECT job_id as jobId, job_type as jobType, project_id as projectId, job_name as jobName, job_alias_name as jobAliasName, instance_id as instanceId, work_flow_dsl AS workFlowDSL, description as description,job_parameters as jobParameters,status as status, data_input as dataInput, data_output as dataOutput, use_yn as useYn, create_user as createUser, create_time as createTime,update_user as updateUser,update_time as updateTime ",
	    "FROM job ",
	    "WHERE job.project_id = #{projectId} "
	})
	List<Job> getJobRelatedWithProject(ProjectInfo project);

	/**
	 * 根据Job昵称取得项目下Job列表
	 *
	 * @param jobCause
	 * @return
	 */
	@SelectProvider(type = JobSqlProvider.class, method = "checkProjectJobByAsName")
	List<Job> checkProjectJobByAsName(JobCause jobCause);

	/**
	 * 取得记录条数
	 *
	 * @param jobCause
	 * @return
	 */
	@SelectProvider(type = JobSqlProvider.class, method = "count")
    int count(JobCause jobCause);

	/**
	 * 根据JobName取得项目下Job列表
	 *
	 * @param cause
	 * @return
	 */
	@SelectProvider(type = JobSqlProvider.class, method = "validJobName")
	List<Job> validJobName(JobCause cause);

	@Select({
		"select job_id as jobId, job_type as jobType, project_id as projectId, job_name as jobName, job_alias_name as jobAliasName, instance_id as instanceId, work_flow_dsl AS workFlowDSL, description,job_parameters as jobParameters,status as status, data_input as dataInput, data_output as dataOutput, warning_config as warningConfig, deploy_user AS deployUserId, deploy_user_name AS deployUserName, create_user AS createUser,create_user_name AS createUserName, use_yn as useYn, create_time as createTime,update_user as updateUser,update_time as updateTime ",
		"from job ", "where job_name = #{jobName} and use_yn = 'Y'" })
	Job selectJobByName(String jobName);
	
	/**
	 * 获取任务状态分布
	 * @param projectId 项目编号
	 * @return status：任务状态  ， count：状态对应任务数量
	 */
	@Select({
        "select ",
	    "count(if(`status`='DEPLOY',1,null)) as `DEPLOY` ,",
	    "count(if(`status`='UNDEPLOY',1,null)) as `UNDEPLOY` ",
	    "from job ",
	    "where project_id=#{projectId} and use_yn='Y'" })
    Map<String, Long> getJobStatusDistribution(long projectId);
	
	/**
     * 根据类型获取所有任务信息
     * @param jobType 1：流式任务 2：批量任务
     * @param onlyUsing true：只取未删除job。 false：全部job，包括删除状态
     * @return 任务信息
     */
    @SelectProvider(type = JobSqlProvider.class, method = "getAllJobByType")
    List<Job> getAllJobByType(@Param("jobType")int jobType, @Param("onlyUsing")boolean onlyUsing);

    /**
     * @param xdExecutions
     * @return
     * @author yunzhong
     * @time 2017年6月27日上午11:47:22
     */
    @Select({ "<script>",
            "SELECT job_id as jobId,job_name as jobName,job_alias_name as jobAliasName,description as jobDescription,status as jobStatus ,create_user_name as createUserName,create_user as createUser,warning_config as warningConfig,project_id as projectId",
            "FROM job  WHERE job.job_name IN ",
            "<foreach item='xdExecution' index='index' collection='xdExecutions'", "open='(' separator=',' close=')'>",
            "#{xdExecution.jobName}", "</foreach>", "</script>" })
    List<Job> getJobs(@Param("xdExecutions")List<XDExecution> xdExecutions);
    
    @Select({ "<script>",
        "select job.job_name as jobName, job.job_alias_name as jobAliasName, job.create_user_name as createUserName,job.description as description,project_info.project_name as projectName,job.update_time as updateTime from job ",
        "join project_info on job.project_id=project_info.project_id where job.job_type=#{jobType} and job.use_yn = 'Y' and `status`='DEPLOY' ",
        "<if test='jobNames!=null and jobNames.size()>0'>",
        "and job.job_name in ",
        "<foreach item='jobName' index='index' collection='jobNames'", "open='(' separator=',' close=')'>",
        "#{jobName}", 
        "</foreach>", 
        "</if>", 
        "order by project_name",
        "</script>" })
    List<Job> getExecutingJobs(@Param("jobType")int jobType, @Param("jobNames")Set<String> jobNames);

    @Select({
			"select job.job_name as jobName, job.job_alias_name as jobAliasName, job.create_user_name as createUserName,job.description as description,job.update_time as updateTime from job ",
            " where use_yn = 'Y' and `status`='DEPLOY' and instance_id = #{instanceId} and job_type = #{jobType}"
	})
	List<Job> getDeployedByInstanceIdAndType(@Param("instanceId") long instanceId,@Param("jobType") int type);
}
