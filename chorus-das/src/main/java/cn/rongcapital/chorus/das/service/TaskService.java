package cn.rongcapital.chorus.das.service;

import cn.rongcapital.chorus.das.entity.Task;
import cn.rongcapital.chorus.das.entity.web.TaskCause;

import java.util.List;

/**
 * 数据任务步骤
 * 
 * @author lengyang
 *
 */
public interface TaskService {
	/**
	 * 保存数据任务步骤
	 *
	 * @param task
	 */
	void saveTask(Task task);

	/**
	 * 查询所有数据任务步骤
	 *
	 * @return
	 */
	List<Task> selectAll();

	/**
	 * 根据任务ID删除关联所有任务步骤信息
	 *
	 * @param jobId
	 */
	void delTaskByJobId(int jobId);

	/**
	 * 根据主键删除任务步骤信息
	 *
	 * @param taskId
	 */
	void delTaskById(int taskId);

	/**
	 * 根据Id查询任务步骤信息
	 *
	 * @param taskId
	 * @return
	 */
	Task selectTask(int taskId);

	/**
	 * 查询项目任务步骤
	 *
	 * @param jobId
	 * @return
	 */
	List<Task> getTaskList(int jobId);

	/**
	 * 根据JobId和步骤名查询任务步骤信息
	 *
	 * @param jobId
	 * @param taskName
	 * @return
	 */
	Task selectTaskByName(int jobId, String taskName);

	/**
	 * 验证任务步骤名重复
	 *
	 * @param cause
	 * @return
	 */
	List<Task> validTaskName(TaskCause cause);
}
