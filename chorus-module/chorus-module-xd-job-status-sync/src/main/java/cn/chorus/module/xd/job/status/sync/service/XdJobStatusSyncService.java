package cn.chorus.module.xd.job.status.sync.service;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Lovett
 */
public interface XdJobStatusSyncService {

    public void syncTimeoutJob(Connection connection, Integer timeOutHours) throws SQLException;
}
