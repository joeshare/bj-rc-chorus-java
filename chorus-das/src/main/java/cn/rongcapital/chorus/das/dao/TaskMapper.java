package cn.rongcapital.chorus.das.dao;

import cn.rongcapital.chorus.das.entity.Task;
import cn.rongcapital.chorus.das.entity.web.TaskCause;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface TaskMapper {

	/**
	 * 添加数据任务步骤
	 *
	 * @param task
	 * @return
	 */
	@Insert({
		"insert into task (task_id, job_id, module_type, module_name,task_name,alias_name,instance_id,data_input,data_output,config, task_dsl, description, variable, create_user, create_user_name, create_time, update_user, update_user_name, update_time ) values (" +
		"#{taskId,jdbcType=INTEGER}, ",
		"#{jobId,jdbcType=INTEGER}, ",
		"#{moduleType,jdbcType=INTEGER}, ",
		"#{moduleName,jdbcType=VARCHAR},",
		"#{taskName,jdbcType=VARCHAR},",
		"#{aliasName,jdbcType=VARCHAR},",
		"#{instanceId,jdbcType=INTEGER},",
		"#{dataInput,jdbcType=VARCHAR},",
		"#{dataOutput,jdbcType=VARCHAR},",
		"#{config,jdbcType=VARCHAR},",
		"#{taskDSL,jdbcType=VARCHAR},",
		"#{description,jdbcType=VARCHAR}, ",
		"#{variable,jdbcType=VARCHAR}, ",
		"#{createUser,jdbcType=VARCHAR}, ",
		"#{createUserName,jdbcType=VARCHAR}, ",
		"#{createTime,jdbcType=TIMESTAMP}, ",
		"#{updateUser,jdbcType=VARCHAR}, ",
		"#{updateUserName,jdbcType=VARCHAR}, ",
		"#{updateTime,jdbcType=TIMESTAMP}) "
	})
	@Options(useGeneratedKeys=true, keyProperty="taskId", keyColumn="task_id")
	int addTask(Task task);

	/**
	 * 列出所有数据任务步骤
	 *
	 * @return
	 */
	@Select({
		"select task_id as taskId, job_id as jobId, module_type as moduleType, module_name as moduleName,task_name as taskName,alias_name as aliasName,data_input as dataInput,data_output as dataOutput,config,task_dsl as taskDSL, description, variable, create_user as createUser, create_time as createTime,update_user as updateUser,update_time as updateTime ",
		" from task "
	})
	List<Task> selectAll();

	/**
	 * 根据任务ID删除数据任务步骤
	 */
	@Delete({ "delete from task where job_id = #{jobId}" })
	int delTaskByJobId(int jobId);

	/**
	 * 根据步骤ID删除数据任务步骤
	 */
	@Delete({ "delete from task where task_id = #{taskId}" })
	int delTaskById(int taskId);

	/**
	 * 根据任务步骤ID查询数据任务步骤信息
	 *
	 * @param taskId
	 */
	@Select({
		"select task_id as taskId, job_id as jobId, module_name as moduleName,task_name as taskName,alias_name as aliasName,data_input as dataInput,data_output as dataOutput,config,task_dsl as taskDSL, description, variable, create_user as createUser, create_time as createTime,update_user as updateUser,update_time as updateTime ",
		"from task ", "where task_id = #{taskId} " })
	Task selectTask(int taskId);

	/**
	 * 根据任务步骤ID查询数据任务步骤信息
	 *
	 * @param jobId
	 */
	@Select({
		"select task_id as taskId, job_id as jobId, module_type as moduleType,module_name as moduleName,task_name as taskName,alias_name as aliasName,instance_id as instanceId,data_input as dataInput,data_output as dataOutput,config,task_dsl as taskDSL, description, variable, create_user as createUser, create_time as createTime,update_user as updateUser,update_time as updateTime ",
		"from task where job_id = #{jobId} order by create_time " })
	List<Task> getTaskList(int jobId);

	/**
	 * 根据JobId和步骤名查询任务步骤信息
	 *
	 * @param cause
	 * @return
	 */
	@Select({
		"select task_id as taskId, job_id as jobId, module_type as moduleType, module_name as moduleName,task_name as taskName,alias_name as aliasName,data_input as dataInput,data_output as dataOutput,config,task_dsl as taskDSL, description, variable, create_user as createUser, create_time as createTime,update_user as updateUser,update_time as updateTime ",
		"from task ", "where job_id = #{jobId} and task_name = #{taskName}" })
	Task selectTaskByName(TaskCause cause);

	/**
	 * 根据TaskName取得项目下Task列表
	 *
	 * @param cause
	 * @return
	 */
	@SelectProvider(type = TaskSqlProvider.class, method = "validTaskName")
	List<Task> validTaskName(TaskCause cause);
}
