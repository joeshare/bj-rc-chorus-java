package cn.rongcapital.chorus.monitor.project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import cn.rongcapital.chorus.monitor.project.service.BatchJobStatisticService;
import cn.rongcapital.chorus.monitor.project.service.StreamJobStatisticService;
import lombok.extern.slf4j.Slf4j;

/**
 * 任务统计Monitor
 * @author kevin.gong
 * @Time 2017年6月20日 下午2:07:52
 */
@Component
@Slf4j
public class JobStatisticMonitor {
    
    @Autowired
    private BatchJobStatisticService batchJobStatisticService;
    
    @Autowired
    private StreamJobStatisticService streamJobStatisticService;
    
    @Scheduled(cron = "${monitor.spring.xd.job.statistic.cron}")
    public void monitorTasks() {
        log.info("start job statistic...");
        try {
            batchJobStatisticService.jobStatistic();
            streamJobStatisticService.jobStatistic();
        } catch (Exception e) {
            log.error("JOB STATISTIC EXCEPTION:", e);
        }
        log.info("end job statistic...");
    }
    
}
