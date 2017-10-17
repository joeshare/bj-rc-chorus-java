package cn.rongcapital.chorus.modules.batchjob.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.springframework.stereotype.Repository;

import cn.rongcapital.chorus.modules.batchjob.dao.XdDao;
import cn.rongcapital.chorus.modules.batchjob.entity.JobExecStatistic;

/**
 * XD库Dao层实现
 * @author kevin.gong
 * @Time 2017年8月9日 上午9:46:20
 */
@Repository
public class XdDaoImpl implements XdDao {

    @Override
    public List<JobExecStatistic> getJobStatisticFromXDExecutions(Connection connection, String statisticDate) throws SQLException {
        QueryRunner qRunner = new QueryRunner(); 
        String sql = "select t1.*,ifnull(t2.currentRunningNum, 0) as currentRunningNum from " +
                "(select " +
                "job_name as jobName," +
                "MAX(TIMESTAMPDIFF(SECOND,bje.START_TIME,bje.END_TIME)) as maxDuration," +
                "AVG(TIMESTAMPDIFF(SECOND,bje.START_TIME,bje.END_TIME)) as avgDuration," +
                "count(if(bje.`STATUS`='COMPLETED',true,null)) as completedNum," +
                "count(if(bje.`STATUS`='FAILED',true,null)) as failedNum," +
                "now() as `currentRunningTime` " +
                "from BATCH_JOB_EXECUTION bje inner join " +
                "BATCH_JOB_INSTANCE bji on bje.JOB_INSTANCE_ID=bji.JOB_INSTANCE_ID " +
                "and date_format(bje.END_TIME,'%Y-%m-%d')=? " +
                "group by jobName) as t1 " +
                "left join " +
                "(select job_name as jobName,count(1) as currentRunningNum from BATCH_JOB_EXECUTION bje inner join " +
                "BATCH_JOB_INSTANCE bji on bje.JOB_INSTANCE_ID=bji.JOB_INSTANCE_ID " +
                "where `STATUS`='STARTED' group by JOB_NAME) as t2 " +
                "on t1.jobName = t2.jobName";
        return qRunner.query(connection, sql, new BeanListHandler<JobExecStatistic>(JobExecStatistic.class), statisticDate);
    }

}
