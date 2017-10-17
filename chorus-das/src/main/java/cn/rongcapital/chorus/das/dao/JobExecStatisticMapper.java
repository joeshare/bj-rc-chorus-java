package cn.rongcapital.chorus.das.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import cn.rongcapital.chorus.das.entity.JobExecStatistic;

/**
 * 流式任务执行统计Mapper
 * @author kevin.gong
 * @Time 2017年6月20日 上午10:03:41
 */
public interface JobExecStatisticMapper {

    /**
     * 获取任务执行分布
     * @param projectId 项目编号
     * @param startDate 开始日期
     * @return date:日期，completedNum：执行完成次数，failedNum：执行失败次数，runningNum：正在执行次数，runNumAtStatTime：统计时正在执行的任务数量
     */
    @Select({
        "select `date`,sum(completed_num) as completedNum,sum(failed_num) as failedNum,sum(current_running_num) as runningNum from job_exec_statistic",
        " where project_id=#{projectId} and `date`>=#{startDate} and `date`<CURDATE() group by `date` order by `date`" })
    List<Map<String, Object>> getJobExecDist(@Param("projectId")long projectId, @Param("startDate")String startDate);
    
    /**
     * 获取执行时间最长的任务
     * @param projectId 项目编号
     * @param size 数量
     * @return jobName：任务名称，seconds：执行时间（单位：秒）
     */
    @Select({
        "select job_alias_name as jobName,max(max_duration) as seconds from job_exec_statistic where `date` between date_add(CURDATE(), interval -10 day) and CURDATE()",
        " and project_id=#{projectId} and max_duration is not null group by jobName order by seconds desc, jobName limit #{size}" })
    List<Map<String, Object>> getLongestExecTimeJob(@Param("projectId")long projectId, @Param("size")int size);
    
	@Insert({
        "INSERT INTO `job_exec_statistic` (`project_id`, `job_name`, `job_alias_name`, `max_duration`, `avg_duration`, `completed_num`, `failed_num`, `running_num`, `current_running_num`, `current_running_time`, `date`) VALUES (",
        "#{projectId,jdbcType=BIGINT}, ",
        "#{jobName,jdbcType=VARCHAR}, ",
        "#{jobAliasName,jdbcType=INTEGER}, ",
        "#{maxDuration,jdbcType=INTEGER},",
        "#{avgDuration,jdbcType=FLOAT},",
        "#{completedNum,jdbcType=INTEGER}, ",
        "#{failedNum,jdbcType=INTEGER},",
        "#{runningNum,jdbcType=INTEGER},",
        "#{currentRunningNum,jdbcType=INTEGER},",
        "#{currentRunningTime,jdbcType=TIMESTAMP},",
        "date_add(CURDATE(), interval -1 day))"
    })
    int add(JobExecStatistic jobExecStatistic);
	
    @Update({
        "update job_exec_statistic set max_duration=#{maxDuration},",
        "avg_duration=#{avgDuration} ",
        "where job_name=#{jobName} and `date`=#{date} "
    })
    int updateDuration(JobExecStatistic jobExecStatistic);
}
