package org.chorus.module.xd.job.status.sync;

import static org.junit.Assert.assertNotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.chorus.module.xd.job.status.sync.XdTaskStatusSyncJob;
import cn.chorus.module.xd.job.status.sync.dao.impl.XdDaoImpl;
import cn.chorus.module.xd.job.status.sync.entity.XDExecution;
import cn.chorus.module.xd.job.status.sync.service.XdJobStatusSyncService;
import cn.chorus.module.xd.job.status.sync.service.impl.XdJobStatusSyncServiceImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {XdDaoImpl.class,XdJobStatusSyncServiceImpl.class})
public class TestXdDao {

    @Autowired
    private XdDaoImpl xdDao;
    @Autowired
    private XdJobStatusSyncService xdJobStatusSyncService;

    @Test
    public void getXDStartedExecution() throws ClassNotFoundException, SQLException{
        Connection connection = XdTaskStatusSyncJob.Connect("com.mysql.jdbc.Driver", "jdbc:mysql://10.200.48.79:3306/xd", "dps", "Dps@10.200.48.MySQL");
        List<XDExecution> startedXdExecution = xdDao.getStartedXdExecution(connection);

        assertNotNull(startedXdExecution);
    }

}
