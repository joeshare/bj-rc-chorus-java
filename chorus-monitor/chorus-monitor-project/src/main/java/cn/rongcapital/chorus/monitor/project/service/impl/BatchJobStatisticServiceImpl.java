package cn.rongcapital.chorus.monitor.project.service.impl;

import static cn.rongcapital.chorus.monitor.project.constant.Const.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.rongcapital.chorus.common.util.CollectionUtils;
import cn.rongcapital.chorus.das.entity.Job;
import cn.rongcapital.chorus.das.entity.JobExecStatistic;
import cn.rongcapital.chorus.das.service.JobExecStatisticService;
import cn.rongcapital.chorus.das.service.JobService;
import cn.rongcapital.chorus.das.xd.dao.XDMapper;
import cn.rongcapital.chorus.monitor.project.service.BatchJobStatisticService;
import lombok.extern.slf4j.Slf4j;

/**
 * 批量任务统计实现类
 * @author kevin.gong
 * @Time 2017年6月22日 上午10:07:07
 */
@Service(value = "BatchJobStatisticService")
@Slf4j
public class BatchJobStatisticServiceImpl implements BatchJobStatisticService {
    
    @Autowired
    private JobService jobService;
    
    @Autowired
    private JobExecStatisticService jobExecStatisticService;
    
    @Autowired
    private XDMapper xdMapper;
    
    @Value(value = "${datasource.xd.driverClassName}")
    private String xdDriverClassName;

    @Value(value = "${datasource.xd.url}")
    private String xdUrl;

    @Value(value = "${datasource.xd.username}")
    private String xdUserName;

    @Value(value = "${datasource.xd.password}")
    private String xdPassword;
    
    static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 批量任务统计
     */
    @Override
    public void jobStatistic(){
        log.info("start batchJob statistic...");
        List<JobExecStatistic> jobExecStatisticList = getBatchJobExecInfoYest();
        List<Job> allJob = jobService.getAllBatchJob();
        if(CollectionUtils.isNotEmpty(jobExecStatisticList) && CollectionUtils.isNotEmpty(allJob)){
            addBatchJobStatisticToDB(jobExecStatisticList, allJob);
        }
        log.info("end batchJob statistic...");
    }
    
    /**
     * 合并数据，插入数据库
     * @param jobExecStatistic XD执行数据的开始时间在前一天的统计信息
     * @param allJob chorus存储的所有的Job信息
     */
    private void addBatchJobStatisticToDB(List<JobExecStatistic> jobExecStatisticList, List<Job> allJob) {
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
                jobExecStatisticService.addBatchJobExecStatistic(jobExecStatistic);
            }
        }
    }
    
    /**
     * 统计昨天批量任务执行信息
     * @return
     */
    private List<JobExecStatistic> getBatchJobExecInfoYest(){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        String yesterday = df.format(cal.getTime());
        return xdMapper.getJobStatisticFromXDExecutions(yesterday);
    }
    
}
