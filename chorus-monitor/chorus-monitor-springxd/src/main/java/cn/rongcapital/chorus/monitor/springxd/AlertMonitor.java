package cn.rongcapital.chorus.monitor.springxd;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import cn.rongcapital.chorus.common.util.CollectionUtils;
import cn.rongcapital.chorus.common.util.SpringBeanUtils;
import cn.rongcapital.chorus.das.entity.XDExecution;
import cn.rongcapital.chorus.das.service.JobFailHistoryService;
import cn.rongcapital.chorus.das.service.JobMonitorService;
import cn.rongcapital.chorus.monitor.springxd.event.MailApplicationEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * 查询所有失败的任务
 * 
 * @author yunzhong
 * @date 2017年5月16日下午2:00:15
 */
@Component
@Slf4j
public class AlertMonitor {
    @Autowired
    private JobMonitorService jobMonitorService;

    @Autowired
    private JobFailHistoryService jobFailHistoryService;

    @Scheduled(cron = "${monitor.spring.xd.task.cron}")
    public void monitorTasks() {
        log.info("start monitor task status.");
        List<String> statusList = new ArrayList<>();
        statusList.add("FAILED");
        int pageNum = 1;
        int pageSize = 100;
        while (true) {
            List<XDExecution> xdExecutions = jobMonitorService.getXDExecutions(pageNum, pageSize, statusList);
            if (CollectionUtils.isEmpty(xdExecutions)) {
                log.info("There is no failed jobs in environment");
                break;
            }
            Set<String> selectExecutionIds = jobFailHistoryService.selectExecutionIds(xdExecutions);
            for (XDExecution xdExecution : xdExecutions) {
                if (selectExecutionIds.contains(String.valueOf(xdExecution.getExecutionId()))) {
                    continue;
                } else {
                    SpringBeanUtils.publish(new MailApplicationEvent(xdExecution));
                }
            }
            pageNum++;
        }

    }

}
