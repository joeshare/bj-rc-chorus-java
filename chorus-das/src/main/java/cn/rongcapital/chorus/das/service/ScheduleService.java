package cn.rongcapital.chorus.das.service;

import cn.rongcapital.chorus.das.entity.Schedule;

import java.util.List;

/**
 * 任务调度
 * 
 * @author lengyang
 *
 */
public interface ScheduleService {
	/**
	 * 保存任务调度
	 *
	 * @param schedule
	 */
	void saveSchedule(Schedule schedule);

	/**
	 * 编辑任务调度
	 *
	 * @param schedule
	 */
	void updateScheduleByJobId(Schedule schedule);

	/**
	 * 查询所有任务调度
	 *
	 * @return
	 */
	List<Schedule> selectAll();

	/**
	 * 根据任务ID删除关联所有任务调度信息
	 *
	 * @param jobId
	 */
	void delScheduleByJobId(int jobId);

	/**
	 * 根据主键删除任务调度信息
	 *
	 * @param scheduleId
	 */
	void delScheduleById(int scheduleId);

	/**
	 * 根据Id查询任务调度信息
	 *
	 * @param scheduleId
	 * @return
	 */
	Schedule selectSchedule(int scheduleId);

	/**
	 * 查询任务调度信息
	 *
	 * @param
	 * @return
	 */
	Schedule getScheduleByJobId(int jobId);
}
