package cn.chorus.module.xd.job.status.sync.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import cn.chorus.module.xd.job.status.sync.entity.XDExecution;

/**
 * @author Lovett
 */
public interface XdDao {

    public List<XDExecution> getStartedXdExecution(Connection connection) throws SQLException;

    public void updateToFailedStatus(Connection connection, Object[][] param) throws SQLException;
}
