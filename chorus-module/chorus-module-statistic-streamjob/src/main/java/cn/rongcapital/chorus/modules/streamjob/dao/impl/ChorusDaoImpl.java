package cn.rongcapital.chorus.modules.streamjob.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.springframework.stereotype.Repository;

import cn.rongcapital.chorus.modules.streamjob.dao.ChorusDao;
import cn.rongcapital.chorus.modules.streamjob.entity.Job;
import cn.rongcapital.chorus.modules.streamjob.entity.StreamExecStatistic;

/**
 * chorus库Dao层实现
 * @author kevin.gong
 * @Time 2017年8月9日 上午9:42:59
 */
@Repository
public class ChorusDaoImpl implements ChorusDao {

    @Override
    public List<Job> getAllStreamJob(Connection connection) throws SQLException {
        QueryRunner qRunner = new QueryRunner();  
        String sql = "select job_id as jobId, job_type as jobType, project_id as projectId, job_name as jobName, "+
                        "job_alias_name as jobAliasName, instance_id as instanceId, work_flow_dsl AS workFlowDSL, description, "+
                        "job_parameters as jobParameters, status as status, data_input as dataInput, data_output as dataOutput, use_yn as useYn, "+
                        "create_user as createUser, create_time as createTime, update_user as updateUser, update_time as updateTime from job " +
                        "where job_type = 1 and use_yn = 'Y' order by projectId,jobId";
        return qRunner.query(connection, sql, new BeanListHandler<Job>(Job.class));   
    }

    @Override
    public boolean addStreamJobStatistic(Connection connection, StreamExecStatistic streamExecStatistic)
            throws SQLException {
        QueryRunner qRunner = new QueryRunner();  
        String sql = "REPLACE INTO `stream_exec_statistic` (`project_id`, `no_exec_num`, `failed_num`, `running_num`, `date`) VALUES " + 
        "(?, ?, ?, ?, date_add(CURDATE(), interval -1 day))";
        return qRunner.update(connection, sql, streamExecStatistic.getProjectId(), 
                streamExecStatistic.getNoExecNum(), streamExecStatistic.getFailedNum(), streamExecStatistic.getRunningNum()) > 0;  
    }


}
