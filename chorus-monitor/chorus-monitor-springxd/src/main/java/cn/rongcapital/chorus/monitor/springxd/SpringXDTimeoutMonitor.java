package cn.rongcapital.chorus.monitor.springxd;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONObject;

import cn.rongcapital.chorus.common.util.JsonUtils;
import cn.rongcapital.chorus.common.util.SpringBeanUtils;
import cn.rongcapital.chorus.common.util.StringUtils;
import cn.rongcapital.chorus.das.entity.Job;
import cn.rongcapital.chorus.das.entity.TaskExecutionTimeout;
import cn.rongcapital.chorus.das.entity.XDExecution;
import cn.rongcapital.chorus.das.service.JobMonitorService;
import cn.rongcapital.chorus.das.service.JobService;
import cn.rongcapital.chorus.das.service.TaskExecutionService;
import cn.rongcapital.chorus.monitor.springxd.event.TimeoutMailApplicationEvent;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SpringXDTimeoutMonitor {
    private static final int DEFAULT_BATCH_SIZE = 100;
    @Autowired
    private JobMonitorService jobMonitorService;
    @Autowired
    private JobService jobService;
    @Autowired
    private TaskExecutionService executionService;

    @Scheduled(cron = "0 * * * * ?")
    public void monitorTimeout() {
        log.info("start to monitor timeout task.");
        int pageNum = 1;
        int pageSize = DEFAULT_BATCH_SIZE;
        List<String> statusList = new ArrayList<>();
        statusList.add("STARTING");
        statusList.add("STARTED");
        statusList.add("STOPPING");

        while (true) {
            List<XDExecution> xdExecutions = jobMonitorService.getXDExecutions(pageNum, pageSize, statusList);
            pageNum++;
            if (CollectionUtils.isEmpty(xdExecutions)) {
                break;
            }
            // 批量获得execution对应的job
            List<Job> jobs = jobService.getJobs(xdExecutions);
            if (CollectionUtils.isEmpty(jobs)) {
                log.debug("There are no timeout jobs of this batch.");
                continue;
            }
            Map<String, Job> jobMap = new HashMap<>();
            jobs.forEach((job) -> {
                jobMap.put(job.getJobName(), job);
            });
            // 是否已经发出告警
            Set<Long> notifiedExecutionIds = executionService.filterNotified(xdExecutions);
            // 遍历execution，查看是否超时
            for (XDExecution xdExecution : xdExecutions) {
                try {
                    if (notifiedExecutionIds.contains(Long.valueOf(xdExecution.getExecutionId()))) {
                        log.debug("task execution [" + xdExecution.getExecutionId() + "] has been notified.");
                        continue;
                    }
                    Job job = jobMap.get(xdExecution.getJobName());
                    if (job == null) {
                        log.debug("There is no job with name [" + xdExecution.getJobName() + "] of execution ["
                                + xdExecution.getExecutionId() + "]");
                        continue;
                    }
                    Date endDate = new Date();
                    Date expectedEndDate = getEndDate(xdExecution, job);
                    if (expectedEndDate != null && expectedEndDate.getTime() <= endDate.getTime()) {
                        TaskExecutionTimeout timeoutRecord = new TaskExecutionTimeout();
                        timeoutRecord.setTaskId(job.getJobId());
                        timeoutRecord.setTaskExecutionId(xdExecution.getExecutionId());
                        timeoutRecord.setStartTime(xdExecution.getStartTime());
                        timeoutRecord.setExpectEndTime(expectedEndDate);
                        timeoutRecord.setJob(job);
                        SpringBeanUtils.publish(new TimeoutMailApplicationEvent(timeoutRecord));
                    }
                } catch (Exception e) {
                    log.warn("Failed to deal job [" + xdExecution.getJobName() + "] with ["
                            + xdExecution.getExecutionId() + "]", e);
                }
            }
        }
    }

    /**
     * @param xdExecution
     * @param job
     * @return
     * @author yunzhong
     * @time 2017年6月27日下午4:42:00
     */
    private Date getEndDate(XDExecution xdExecution, Job job) {
        if (xdExecution == null || job == null) {
            return null;
        }
        try {
            Date startTime = xdExecution.getStartTime();
            String warningConfig = job.getWarningConfig();
            if (StringUtils.isEmpty(warningConfig)) {
                return null;
            }
            JSONObject jsonObject = JsonUtils.toJsonObject(warningConfig);
            String intervalMinute = jsonObject.getString("timeOutInterval");
            return new Date(startTime.getTime() + Long.valueOf(intervalMinute) * 60 * 1000L);
        } catch (Exception e) {
            log.error("Failed to assert time out of job [" + job.getJobAliasName() + "] of execution ["
                    + xdExecution.getExecutionId() + "]", e);
        }
        return null;
    }
}
