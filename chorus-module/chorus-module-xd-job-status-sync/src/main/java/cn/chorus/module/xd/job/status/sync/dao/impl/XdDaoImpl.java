package cn.chorus.module.xd.job.status.sync.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.springframework.stereotype.Repository;

import cn.chorus.module.xd.job.status.sync.dao.XdDao;
import cn.chorus.module.xd.job.status.sync.entity.XDExecution;

/**
 * @author Lovett
 */
@Repository
public class XdDaoImpl implements XdDao {

    @Override
    public List<XDExecution> getStartedXdExecution(Connection connection) throws SQLException{
        QueryRunner qRunner = new QueryRunner();
        String sql = "SELECT xe.JOB_EXECUTION_ID as executionId, xe.JOB_INSTANCE_ID as instanceId,xe.START_TIME as startTime,xe.END_TIME as endTime,xe.STATUS as status " +
                "FROM xd.BATCH_JOB_EXECUTION xe "+
                "WHERE xe.STATUS = 'STARTED'";

        return qRunner.query(connection, sql, new BeanListHandler<XDExecution>(XDExecution.class));
    }

    public void updateToFailedStatus(Connection connection, Object[][] param) throws SQLException {
        QueryRunner qRunner = new QueryRunner();
        String sql = "UPDATE xd.BATCH_JOB_EXECUTION set STATUS = 'FAILED' where JOB_EXECUTION_ID = ?";

        qRunner.batch(connection, sql, param);
    }

}
