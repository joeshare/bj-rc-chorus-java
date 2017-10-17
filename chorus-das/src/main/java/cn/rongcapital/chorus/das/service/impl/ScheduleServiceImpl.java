package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.das.dao.ScheduleMapper;
import cn.rongcapital.chorus.das.entity.Schedule;
import cn.rongcapital.chorus.das.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 任务调度管理SERVICE实现类
 *
 * @author lengyang
 */
@Service(value = "ScheduleService")
@Transactional
public class ScheduleServiceImpl implements ScheduleService {

	@Autowired(required = false)
	private ScheduleMapper scheduleMapper;

	/**
	 * 添加任务调度
	 *
	 * @param schedule
	 *            任务调度
	 */
	@Override
	public void saveSchedule(Schedule schedule) {
		scheduleMapper.addSchedule(schedule);
	}

	/**
	 * 编辑任务调度
	 *
	 * @param schedule
	 *            任务调度
	 */
	@Override
	public void updateScheduleByJobId(Schedule schedule) {
		scheduleMapper.updateSchedule(schedule);
	}

	/**
	 * 查询所有调度
	 *
	 */
	@Override
	public List<Schedule> selectAll() {
		List<Schedule> list = scheduleMapper.selectAll();
		return list;
	}

	@Override
	public void delScheduleByJobId(int jobId) {
		// 删除任务
		scheduleMapper.delScheduleByJobId(jobId);
	}

	@Override
	public void delScheduleById(int scheduleId) {
		// 删除任务
		scheduleMapper.delScheduleById(scheduleId);
	}

	@Override
	public Schedule selectSchedule(int scheduleId) {
		return scheduleMapper.selectSchedule(scheduleId);
	}

	@Override
	public Schedule getScheduleByJobId(int jobId) {
		return scheduleMapper.getScheduleByJobId(jobId);
	}

}
