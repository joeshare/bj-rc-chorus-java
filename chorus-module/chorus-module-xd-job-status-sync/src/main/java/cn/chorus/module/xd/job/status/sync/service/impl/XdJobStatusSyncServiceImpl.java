package cn.chorus.module.xd.job.status.sync.service.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.chorus.module.xd.job.status.sync.dao.XdDao;
import cn.chorus.module.xd.job.status.sync.entity.XDExecution;
import cn.chorus.module.xd.job.status.sync.service.XdJobStatusSyncService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Lovett
 */
@Service(value = "XdJobStatusSyncService")
@Slf4j
public class XdJobStatusSyncServiceImpl implements XdJobStatusSyncService{
    @Autowired
    private XdDao xdDao;

    @Override
    @Transactional
    public void syncTimeoutJob(Connection connection, Integer timeOutHours) throws SQLException {
        List<XDExecution> startedXdExecution = xdDao.getStartedXdExecution(connection);

        if (startedXdExecution != null) {
            Integer[] array = startedXdExecution.stream().filter(e -> isTimeOutDate(e.getStartTime(), timeOutHours))
                    .map(XDExecution::getExecutionId).collect(Collectors.toList()).toArray(new Integer[0]);

            int[] idArray = Arrays.stream(array).mapToInt(Integer::intValue).toArray();
            Object[][] ids = oneDegreeToTwoDegree(idArray, idArray.length, 1);

            log.info("start sync xd status job  ======> numbers : " + idArray.length);

            if (ids.length > 0) {
                xdDao.updateToFailedStatus(connection, ids);
            }

            log.info("end of sync xd status job ======<");
        }
    }

    private boolean isTimeOutDate(Date executionStartDate, Integer timeOutHours) {
        Date currentDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(executionStartDate);
        cal.add(Calendar.HOUR_OF_DAY, +timeOutHours);
        executionStartDate = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String ldate = sdf.format(executionStartDate);
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
        try {
            executionStartDate = sdf.parse(ldate);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        if (currentDate.after(executionStartDate)) {
            return true;
        }

        return false;
    }

    private static Object[][] oneDegreeToTwoDegree(int[] oneDegree, int row, int col) {
        int k = 0;
        Object[][] twoDegree = new Object[row][col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                twoDegree[i][j] = oneDegree[k];
                k++;
            }
        }

        return twoDegree;
    }
}
