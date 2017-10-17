package cn.rongcapital.chorus.modules.batchjob.entity;

import java.util.Date;

/**
 * t_quartz表映射BEAN
 *
 * @author HANS
 */
public class Schedule extends CommonEntity {

	/**
	 * 主键
	 */
	private Integer scheduleId;
	/**
	 * 数据任务表ID
	 */
	private Integer jobId;

	/**
	 * 执行计划名称
	 */
	private String scheduleName;

	/**
	 * 定时任务状态(0:禁用;1:启用;2:运行结束)
	 */
	private Integer scheduleStat;

	/**
	 * 任务类型(1:一次性;2:周期)
	 */
	private Integer scheduleType;

	/**
	 * 调度周期(1:日;2:周3;月)
	 */
	private Integer scheduleCycle;

	/**
	 * 开始时间
	 */
	private Date startTime;

	/**
	 * 结束时间
	 */
	private Date endTime;

	/**
	 * 重复次数(执行次数=1(开始时间执行的一次)+重复次数,0:不重复执行;-1:不限制次数;大于0:具体次数)
	 */
	private Integer repeatCount;

	/**
	 * 重复周期(单位:小时/分钟)
	 */
	private Integer repeatInterval;

	/**
	 * 执行时间-秒(0~59)
	 */
	private String second;

	/**
	 * 执行时间-分(0~59)
	 */
	private String minute;

	/**
	 * 执行时间-时(0~23)
	 */
	private String hour;

	/**
	 * 执行时间-日(1~31)
	 */
	private String day;

	/**
	 * 执行时间-周(1~7,周日是1)
	 */
	private String week;

	/**
	 * 执行时间-月(1~12)
	 */
	private String month;

	/**
	 * CRON表达式
	 */
	private String cronExpression;

	public Integer getScheduleId() {
		return scheduleId;
	}

	public void setScheduleId(Integer scheduleId) {
		this.scheduleId = scheduleId;
	}

	public Integer getJobId() {
		return jobId;
	}

	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}

	public String getScheduleName() {
		return scheduleName;
	}

	public void setScheduleName(String scheduleName) {
		this.scheduleName = scheduleName;
	}

	public Integer getScheduleStat() {
		return scheduleStat;
	}

	public void setScheduleStat(Integer scheduleStat) {
		this.scheduleStat = scheduleStat;
	}

	public Integer getScheduleType() {
		return scheduleType;
	}

	public void setScheduleType(Integer scheduleType) {
		this.scheduleType = scheduleType;
	}

	public Integer getScheduleCycle() {
		return scheduleCycle;
	}

	public void setScheduleCycle(Integer scheduleCycle) {
		this.scheduleCycle = scheduleCycle;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Integer getRepeatCount() {
		return repeatCount;
	}

	public void setRepeatCount(Integer repeatCount) {
		this.repeatCount = repeatCount;
	}

	public Integer getRepeatInterval() {
		return repeatInterval;
	}

	public void setRepeatInterval(Integer repeatInterval) {
		this.repeatInterval = repeatInterval;
	}

	public String getSecond() {
		return second;
	}

	public void setSecond(String second) {
		this.second = second;
	}

	public String getMinute() {
		return minute;
	}

	public void setMinute(String minute) {
		this.minute = minute;
	}

	public String getHour() {
		return hour;
	}

	public void setHour(String hour) {
		this.hour = hour;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getWeek() {
		return week;
	}

	public void setWeek(String week) {
		this.week = week;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}
}
