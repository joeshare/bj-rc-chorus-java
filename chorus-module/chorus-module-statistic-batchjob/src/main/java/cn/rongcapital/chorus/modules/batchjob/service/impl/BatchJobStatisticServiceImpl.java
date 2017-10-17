package cn.rongcapital.chorus.modules.batchjob.service.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import cn.rongcapital.chorus.modules.batchjob.dao.ChorusDao;
import cn.rongcapital.chorus.modules.batchjob.dao.XdDao;
import cn.rongcapital.chorus.modules.batchjob.entity.Job;
import cn.rongcapital.chorus.modules.batchjob.entity.JobExecStatistic;
import cn.rongcapital.chorus.modules.batchjob.service.BatchJobStatisticService;
import lombok.extern.slf4j.Slf4j;

/**
 * 批量任务统计实现类
 * @author kevin.gong
 * @Time 2017年8月8日 下午2:00:45
 */
@Service(value = "BatchJobStatisticService")
@Slf4j
public class BatchJobStatisticServiceImpl implements BatchJobStatisticService {
    
    private static final String KEY_PROJECT_ID = "project_id";
    private static final String KEY_JOB_ALIAS_NAME = "job_alias_name";
    
    @Autowired
    private ChorusDao chorusDao;
    
    @Autowired
    private XdDao xdDao;
    
    static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 批量任务统计
     * @throws SQLException 
     */
    @Override
    public void jobStatistic(Connection chorusConn, Connection xdConn) throws SQLException{
        log.info("start batchJob statistic...");
        List<JobExecStatistic> jobExecStatisticList = getBatchJobExecInfoYest(xdConn);
        List<Job> allJob = chorusDao.getAllBatchJob(chorusConn);
        if(!CollectionUtils.isEmpty(jobExecStatisticList) && !CollectionUtils.isEmpty(allJob)){
            addBatchJobStatisticToDB(chorusConn, jobExecStatisticList, allJob);
        }
        log.info("end batchJob statistic...");
    }
    
    /**
     * 合并数据，插入数据库
     * @param jobExecStatistic XD执行数据的开始时间在前一天的统计信息
     * @param allJob chorus存储的所有的Job信息
     * @throws SQLException 
     */
    private void addBatchJobStatisticToDB(Connection chorusConn, List<JobExecStatistic> jobExecStatisticList, List<Job> allJob) throws SQLException {
        Map<String, Map<String, Object>> jobMap = new HashMap<>();
        for (Job job : allJob) {
            Map<String, Object> jobSubMap = new HashMap<>();
            jobSubMap.put(KEY_PROJECT_ID, job.getProjectId());
            jobSubMap.put(KEY_JOB_ALIAS_NAME, job.getJobAliasName());
            jobMap.put(job.getJobName(), jobSubMap);
        }
        
        //插入统计出的昨天执行的任务信息
        for (JobExecStatistic jobExecStatistic : jobExecStatisticList) {
            Map<String, Object> jobSubMap = jobMap.get(jobExecStatistic.getJobName());
            //jobSubMap为空时，表示不是chorus的任务
            if(jobSubMap != null) {
                jobExecStatistic.setProjectId(((Integer)jobSubMap.get(KEY_PROJECT_ID)).longValue());
                jobExecStatistic.setJobAliasName((String)jobSubMap.get(KEY_JOB_ALIAS_NAME));
                chorusDao.addBatchJobExecStatistic(chorusConn, jobExecStatistic);
            }
        }
    }
    
    /**
     * 统计昨天批量任务执行信息
     * @return
     * @throws SQLException 
     */
    private List<JobExecStatistic> getBatchJobExecInfoYest(Connection xdConn) throws SQLException{
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        String yesterday = df.format(cal.getTime());
        return xdDao.getJobStatisticFromXDExecutions(xdConn, yesterday);
    }
    
}
