package cn.rongcapital.chorus.das.service.impl;

import java.util.List;

import cn.rongcapital.chorus.das.dao.TaskMapper;
import cn.rongcapital.chorus.das.entity.Task;
import cn.rongcapital.chorus.das.entity.web.JobCause;
import cn.rongcapital.chorus.das.service.TaskService;
import cn.rongcapital.chorus.das.service.OperLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.rongcapital.chorus.das.entity.web.TaskCause;
import cn.rongcapital.chorus.das.entity.web.R;

/**
 * 数据任务步骤管理SERVICE实现类
 *
 * @author lengyang
 */
@Service(value = "TaskService")
@Transactional
public class TaskServiceImpl implements TaskService {

	@Autowired(required = false)
	private TaskMapper taskMapper;

	/**
	 * 添加步骤
	 *
	 * @param task
	 *            步骤信息
	 */
	@Override
	public void saveTask(Task task) {
		taskMapper.addTask(task);
	}

	/**
	 * 查询所有步骤
	 *
	 * 步骤
	 */
	@Override
	public List<Task> selectAll() {
		List<Task> list = taskMapper.selectAll();
		return list;
	}

	@Override
	public void delTaskByJobId(int jobId) {
		// 删除任务
		taskMapper.delTaskByJobId(jobId);
	}

	@Override
	public void delTaskById(int stepId) {
		// 删除任务
		taskMapper.delTaskById(stepId);
	}

	@Override
	public Task selectTask(int stepId) {
		return taskMapper.selectTask(stepId);
	}

	@Override
	public Task selectTaskByName(int jobId, String taskName) {
		TaskCause taskCause = new TaskCause();
		taskCause.setJobId(jobId);
		taskCause.setTaskName(taskName);
		return taskMapper.selectTaskByName(taskCause);
	}

	@Override
	public List<Task> validTaskName(TaskCause cause) {
		return taskMapper.validTaskName(cause);
	}

	@Override
	public List<Task> getTaskList(int jobId) {
		return taskMapper.getTaskList(jobId);
	}

}
