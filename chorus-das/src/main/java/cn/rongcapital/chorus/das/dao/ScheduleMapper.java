package cn.rongcapital.chorus.das.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import cn.rongcapital.chorus.das.entity.Schedule;

public interface ScheduleMapper {

	/**
	 * 添加数据任务步骤
	 *
	 * @param schedule
	 * @return
	 */
	@Insert({
		"insert into schedule (schedule_id,job_id,schedule_name,schedule_stat,schedule_type,schedule_cycle,start_time,end_time,repeat_count," +
		"repeat_interval,second,minute,hour,day,week,month,cron_expression,remark, create_user, create_time, " +
				"update_user, update_time ) values (" +
		"#{scheduleId,jdbcType=INTEGER}, ",
		"#{jobId,jdbcType=INTEGER}, ",
		"#{scheduleName,jdbcType=VARCHAR}, ",
		"#{scheduleStat,jdbcType=DECIMAL}, " ,
		"#{scheduleType,jdbcType=DECIMAL}, " ,
		"#{scheduleCycle,jdbcType=DECIMAL}, " ,
		"#{startTime,jdbcType=TIMESTAMP}, " ,
		"#{endTime,jdbcType=TIMESTAMP}, " ,
		"#{repeatCount,jdbcType=DECIMAL}, " ,
		"#{repeatInterval,jdbcType=DECIMAL}, " ,
		"#{second,jdbcType=VARCHAR}, " ,
		"#{minute,jdbcType=VARCHAR}, " ,
		"#{hour,jdbcType=VARCHAR}, " ,
		"#{day,jdbcType=VARCHAR}, " ,
		"#{week,jdbcType=VARCHAR}, " ,
		"#{month,jdbcType=VARCHAR}, ",
		"#{cronExpression,jdbcType=VARCHAR}, ",
		"#{remark,jdbcType=VARCHAR}, ",
		"#{createUser,jdbcType=VARCHAR}, ",
		"#{createTime,jdbcType=TIMESTAMP}, ",
		"#{updateUser,jdbcType=VARCHAR}, ",
		"#{updateTime,jdbcType=TIMESTAMP}) "
	})
	@Options(useGeneratedKeys=true, keyProperty="scheduleId", keyColumn="schedule_id")
	int addSchedule(Schedule schedule);

	/**
	 * 更新
	 * @param schedule
	 * @return
	 */
	@Update({
		"update schedule set schedule_name=#{scheduleName},",
		"schedule_stat=#{scheduleStat},",
		"schedule_type=#{scheduleType},",
		"schedule_cycle=#{scheduleCycle},",
		"start_time=#{startTime},",
		"end_time=#{endTime},",
		"repeat_count=#{repeatCount},",
		"repeat_interval=#{repeatInterval},",
		"second=#{second},",
		"minute=#{minute},",
		"hour=#{hour},",
		"day=#{day},",
		"week=#{week},",
		"month=#{month},",
		"cron_expression=#{cronExpression},",
		"remark=#{remark},",
		"update_user=#{updateUser},",
		"update_time=#{updateTime}",
		"where job_id=#{jobId} "
	})
	int updateSchedule(Schedule schedule);

	/**
	 * 列出所有数据任务步骤
	 *
	 * @return
	 */
	@Select({
		"select schedule_id as scheduleId,job_id as jobId,schedule_name as scheduleName,schedule_stat as scheduleStat,schedule_type as scheduleType,schedule_cycle as scheduleCycle,start_time as startTime,end_time as endTime,repeat_count as repeatCount," +
		"repeat_interval as repeatInterval,second,minute,hour,day,week,month,cron_expression as cronExpression, remark create_user as createUser, create_time as createTime, update_user as updateUser, update_time as updateTime ",
		" from schedule "
	})
	List<Schedule> selectAll();

	/**
	 * 根据任务ID删除数据任务步骤
	 */
	@Delete({ "delete from schedule where job_id = #{jobId}" })
	int delScheduleByJobId(int jobId);

	/**
	 * 根据步骤ID删除数据任务步骤
	 */
	@Delete({ "delete from schedule where schedule_id = #{scheduleId}" })
	int delScheduleById(int scheduleId);

	/**
	 * 根据任务步骤ID查询数据任务步骤信息
	 *
	 * @param c
	 * @param id
	 */
	@Select({
		"select schedule_id as scheduleId,job_id as jobId,schedule_name as scheduleName,schedule_stat as scheduleStat,schedule_type as scheduleType,schedule_cycle as scheduleCycle,start_time as startTime,end_time as endTime,repeat_count as repeatCount," +
		"repeat_interval as repeatInterval,second,minute,hour,day,week,month,cron_expression as cronExpression, remark create_user as createUser, create_time as createTime, update_user as updateUser, update_time as updateTime ",
		"from schedule ", "where schedule_id = #{scheduleId} " })
	Schedule selectSchedule(int schedule_id);

	/**
	 * 根据任务步骤ID查询数据任务步骤信息
	 *
	 * @param c
	 * @param id
	 */
	@Select({
		"select schedule_id as scheduleId,job_id as jobId,schedule_name as scheduleName,schedule_stat as scheduleStat,schedule_type as scheduleType,schedule_cycle as scheduleCycle,start_time as startTime,end_time as endTime,repeat_count as repeatCount," +
		"repeat_interval as repeatInterval,second,minute,hour,day,week,month,cron_expression as cronExpression, remark, create_user as createUser, create_time as createTime, update_user as updateUser, update_time as updateTime ",
		"from schedule where job_id = #{jobId} order by create_time limit 1" })
	Schedule getScheduleByJobId(int jobId);

}
