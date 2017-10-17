package cn.rongcapital.chorus.modules.batchjob.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.springframework.stereotype.Repository;

import cn.rongcapital.chorus.modules.batchjob.dao.ChorusDao;
import cn.rongcapital.chorus.modules.batchjob.entity.Job;
import cn.rongcapital.chorus.modules.batchjob.entity.JobExecStatistic;

/**
 * chorus库Dao层实现
 * @author kevin.gong
 * @Time 2017年8月9日 上午9:46:04
 */
@Repository
public class ChorusDaoImpl implements ChorusDao {

    @Override
    public List<Job> getAllBatchJob(Connection connection) throws SQLException {
        QueryRunner qRunner = new QueryRunner();  
        String sql = "select job_id as jobId, job_type as jobType, project_id as projectId, job_name as jobName, "+
                        "job_alias_name as jobAliasName, instance_id as instanceId, work_flow_dsl AS workFlowDSL, description, "+
                        "job_parameters as jobParameters, status as status, data_input as dataInput, data_output as dataOutput, use_yn as useYn, "+
                        "create_user as createUser, create_time as createTime, update_user as updateUser, update_time as updateTime from job " +
                        "where job_type = 2 order by projectId,jobId";
        return qRunner.query(connection, sql, new BeanListHandler<Job>(Job.class));   
    }

    @Override
    public boolean addBatchJobExecStatistic(Connection connection, JobExecStatistic jobExecStatistic) throws SQLException {
        QueryRunner qRunner = new QueryRunner();  
        String sql = "REPLACE INTO `job_exec_statistic` (`project_id`, `job_name`, `job_alias_name`, `max_duration`, `avg_duration`, `completed_num`, `failed_num`, `running_num`, `current_running_num`, `current_running_time`, `date`) VALUES " +
                "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, date_add(CURDATE(), interval -1 day))"; 
        return qRunner.update(connection, sql, jobExecStatistic.getProjectId(), jobExecStatistic.getJobName(), jobExecStatistic.getJobAliasName(),
                jobExecStatistic.getMaxDuration(), jobExecStatistic.getAvgDuration(), jobExecStatistic.getCompletedNum(),
                jobExecStatistic.getFailedNum(), jobExecStatistic.getRunningNum(), jobExecStatistic.getCurrentRunningNum(),
                jobExecStatistic.getCurrentRunningTime()) > 0;  
    }

}
