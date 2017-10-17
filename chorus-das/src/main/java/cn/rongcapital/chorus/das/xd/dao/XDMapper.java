package cn.rongcapital.chorus.das.xd.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import cn.rongcapital.chorus.das.entity.JobExecStatistic;
import cn.rongcapital.chorus.das.entity.JobMonitor;
import cn.rongcapital.chorus.das.entity.XDExecution;
import cn.rongcapital.chorus.das.entity.XDTaskNum;
import cn.rongcapital.chorus.das.entity.web.JobMonitorCause;

/**
 * @author Li.ZhiWei
 */
public interface XDMapper {
	@Select({
			"SELECT xe.JOB_EXECUTION_ID as executionId, xe.JOB_INSTANCE_ID as instanceId,xe.START_TIME as startTime,xe.END_TIME as endTime,xe.STATUS as status,xi.JOB_NAME as jobName ",
			"FROM xd.BATCH_JOB_EXECUTION xe ,xd.BATCH_JOB_INSTANCE xi ",
			"WHERE xe.JOB_INSTANCE_ID = xi.JOB_INSTANCE_ID ", "ORDER BY xe.JOB_EXECUTION_ID DESC" })
	List<XDExecution> getAllXDExecutions();

	@Select({ "<script>",
			"SELECT xe.JOB_EXECUTION_ID as executionId, xe.JOB_INSTANCE_ID as instanceId,xe.START_TIME as startTime,xe.END_TIME as endTime,xe.STATUS as status,xi.JOB_NAME as jobName ",
			"FROM xd.BATCH_JOB_EXECUTION xe ,xd.BATCH_JOB_INSTANCE xi ",
			"WHERE xe.JOB_INSTANCE_ID = xi.JOB_INSTANCE_ID and xe.STATUS IN ",
			"<foreach item='item' collection='statusList'", "open='(' separator=',' close=')'>", "#{item}",
			"</foreach>", "ORDER BY xe.JOB_EXECUTION_ID DESC limit #{pageSize} offset #{offset}", "</script>" })
	List<XDExecution> getXDExecutions(@Param("offset") int offset, @Param("pageSize") int pageSize,
			@Param("statusList") List<String> statusList);
	
	@Select({ "select bje.JOB_EXECUTION_ID as executionId,bje.JOB_INSTANCE_ID as instanceId,bji.JOB_NAME as jobName,bje.START_TIME as startTime from BATCH_JOB_EXECUTION bje ",
	        "join BATCH_JOB_INSTANCE bji on bje.JOB_INSTANCE_ID=bji.JOB_INSTANCE_ID where `STATUS`='STARTED' and bji.JOB_NAME like 'chorus_%' order by START_TIME desc"})
	List<XDExecution> getAllChorusExecutingXDExecutions();

	@Select({ "<script>",
			"SELECT xe.JOB_EXECUTION_ID as jobExecutionId, xe.JOB_INSTANCE_ID as jobInstanceId,xe.START_TIME as jobStartTime,xe.END_TIME as jobStopTime,xe.STATUS as jobExecuteStatus,xi.JOB_NAME as jobName ",
			"FROM xd.BATCH_JOB_EXECUTION xe ,xd.BATCH_JOB_INSTANCE xi ",
			"WHERE xe.JOB_INSTANCE_ID = xi.JOB_INSTANCE_ID ", "AND xi.JOB_NAME IN ",
			"<foreach item='item' index='index' collection='jobMonitors'", "open='(' separator=',' close=')'>",
			"#{item.jobName}", "</foreach>", "<if test='jobMonitorCause.getExecutionStatus().length > 0'>",
			"AND xe.STATUS IN ", "<foreach item='item' index='index' collection='jobMonitorCause.getExecutionStatus()'",
			"open='(' separator=',' close=')'>", "#{item}", "</foreach>", "</if>", "ORDER BY xe.JOB_EXECUTION_ID DESC ",
			"</script>" })
	List<JobMonitor> getJobMonitorFromXDExecutions(@Param("jobMonitors") List<JobMonitor> jobMonitors,
			@Param("jobMonitorCause") JobMonitorCause jobMonitorCause);

	@Select({ "<script>",
			"SELECT xe.JOB_EXECUTION_ID as jobExecutionId, xe.JOB_INSTANCE_ID as jobInstanceId,xe.START_TIME as jobStartTime,xe.END_TIME as jobStopTime,xe.STATUS as jobExecuteStatus,xi.JOB_NAME as jobName ",
			"FROM xd.BATCH_JOB_EXECUTION xe ,xd.BATCH_JOB_INSTANCE xi ",
			"WHERE xe.JOB_INSTANCE_ID = xi.JOB_INSTANCE_ID ", "AND xe.JOB_EXECUTION_ID IN ",
			"<foreach item='item' index='index' collection='jobMonitors'", "open='(' separator=',' close=')'>",
			"#{item.jobExecutionId}", "</foreach>", "ORDER BY xe.START_TIME DESC ",
			"<if test='jobMonitorCause.getStartIndex() != null'>",
			"LIMIT #{jobMonitorCause.startIndex},#{jobMonitorCause.rowCnt}", "</if>", "</script>" })
	List<JobMonitor> getLimitJobMonitorFromXDExecutions(@Param("jobMonitors") List<JobMonitor> jobMonitors,
			@Param("jobMonitorCause") JobMonitorCause jobMonitorCause);

	@Select({ "<script>", "SELECT COUNT(1) ", "FROM xd.BATCH_JOB_EXECUTION xe ,xd.BATCH_JOB_INSTANCE xi ",
			"WHERE xe.JOB_INSTANCE_ID = xi.JOB_INSTANCE_ID ", "AND xi.JOB_NAME IN ",
			"<foreach item='item' index='index' collection='jobMonitors'", "open='(' separator=',' close=')'>",
			"#{item.jobName}", "</foreach>", "</script>" })
	int getCountJobMonitorFromXDExecutions(@Param("jobMonitors") List<JobMonitor> jobMonitors);
	
	@Select({ "select t1.*,ifnull(t2.currentRunningNum, 0) as currentRunningNum from ",
        "(select ",
        "job_name as jobName,",
        "MAX(TIMESTAMPDIFF(SECOND,bje.START_TIME,bje.END_TIME)) as maxDuration,",
        "AVG(TIMESTAMPDIFF(SECOND,bje.START_TIME,bje.END_TIME)) as avgDuration,",
        "count(if(bje.`STATUS`='COMPLETED',true,null)) as completedNum,",
        "count(if(bje.`STATUS`='FAILED',true,null)) as failedNum,",
        "now() as `currentRunningTime` ",
        "from BATCH_JOB_EXECUTION bje inner join ",
        "BATCH_JOB_INSTANCE bji on bje.JOB_INSTANCE_ID=bji.JOB_INSTANCE_ID ",
        "and date_format(bje.END_TIME,'%Y-%m-%d')=#{statisticDate} ",
        "group by jobName) as t1 ",
        "left join ",
        "(select job_name as jobName,count(1) as currentRunningNum from BATCH_JOB_EXECUTION bje inner join ",
        "BATCH_JOB_INSTANCE bji on bje.JOB_INSTANCE_ID=bji.JOB_INSTANCE_ID ",
        "where `STATUS`='STARTED' group by JOB_NAME) as t2 ",
        "on t1.jobName = t2.jobName" })
	List<JobExecStatistic> getJobStatisticFromXDExecutions(@Param("statisticDate")String statisticDate);
	
	/**
	 * 统计开始时间在前天或更早之前，结束时间在昨天统计任务结束后的任务。（更新当天平均执行时间，最大执行时间用）
	 * @param startTime 开始时间
	 * @param endTime 结束时间
	 * @return 统计结果
	 */
	@Select({ "select ",
    	"distinct ",
    	"bji.job_name as jobName, ",
    	"date_format(bje.START_TIME, '%Y-%m-%d') as `date`, ",
    	"MAX(TIMESTAMPDIFF(SECOND,bje.START_TIME,bje.END_TIME)) as maxDuration, ",
    	"AVG(TIMESTAMPDIFF(SECOND,bje.START_TIME,bje.END_TIME)) as avgDuration ",
    	"from BATCH_JOB_EXECUTION bje inner join ",
    	"BATCH_JOB_INSTANCE bji on bje.JOB_INSTANCE_ID=bji.JOB_INSTANCE_ID ",
    	"inner join ",
    	"(select ",
    	"date_format(t1.START_TIME, '%Y-%m-%d') as `date`, ",
    	"t2.job_name as jobName ",
    	"from BATCH_JOB_EXECUTION t1 inner join BATCH_JOB_INSTANCE t2 on t1.JOB_INSTANCE_ID = t2.JOB_INSTANCE_ID ",
    	"and t1.START_TIME<#{startTime} and t1.END_TIME>=#{endTime} ",
    	"and t1.`STATUS` in ('COMPLETED', 'FAILED')) as temp ",
    	"on bji.job_name = temp.jobName and date_format(bje.START_TIME, '%Y-%m-%d') = temp.`date` ",
    	"group by bji.job_name,date_format(bje.START_TIME, '%Y-%m-%d')" })
	List<JobExecStatistic> getJobStatisticYestEndDataFromXDExec(@Param("startTime")String startTime, @Param("endTime")String endTime);

	@Select({"select sum(if(`STATUS`='COMPLETED',1, 0))as successCount,count(1) total from BATCH_JOB_EXECUTION" })
	XDTaskNum getPlatformTaskSuccessNum();

	@Select({"<script>",
			"select bje.JOB_EXECUTION_ID as executionId,bje.JOB_INSTANCE_ID as instanceId,bji.JOB_NAME as jobName,bje.START_TIME as startTime from BATCH_JOB_EXECUTION bje ",
			"join BATCH_JOB_INSTANCE bji on bje.JOB_INSTANCE_ID=bji.JOB_INSTANCE_ID " ,
			"where `STATUS`='STARTED' " ,
			"and bji.JOB_NAME in " ,
			"<foreach item='item' index='index' collection='jobNames'", "open='(' separator=',' close=')'>#{item}</foreach>",
			" order by START_TIME desc",
			"</script>"
	})
	List<XDExecution> getExecutingXDExecutions(@Param("jobNames") List<String> jobNames);
	
	
    @Select({ "<script>",
            "SELECT xe.JOB_EXECUTION_ID as executionId, xe.JOB_INSTANCE_ID as instanceId,xe.START_TIME as startTime,xe.END_TIME as endTime,xe.STATUS as status,xi.JOB_NAME as jobName ",
            "FROM xd.BATCH_JOB_EXECUTION xe ,xd.BATCH_JOB_INSTANCE xi ",
            "WHERE xe.JOB_INSTANCE_ID = xi.JOB_INSTANCE_ID ",
            "and xe.STATUS IN <foreach item='item' collection='statusList' ",
            "open='(' separator=',' close=')'>#{item}</foreach> ", " and xi.JOB_NAME = #{jobName} ",
            "ORDER BY xe.JOB_EXECUTION_ID DESC ", "</script>" })
    List<XDExecution> getJobExecutions(@Param("jobName") String jobName, @Param("statusList") List<String> statusList);
    
    @Select({"select JOB_EXECUTION_ID as executionId from BATCH_JOB_EXECUTION ",
            "where START_TIME>=#{preStartTime} and START_TIME<=#{currStartTime} and END_TIME is null and `STATUS`='STARTED';"})
    List<Long> getRunningJobExecIdAtStopXDTime(@Param("preStartTime") String preStartTime, @Param("currStartTime") String currStartTime);
}
