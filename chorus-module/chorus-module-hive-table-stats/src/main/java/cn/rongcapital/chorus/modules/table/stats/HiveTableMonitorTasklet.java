package cn.rongcapital.chorus.modules.table.stats;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import cn.rongcapital.chorus.modules.utils.retry.SimpleTasklet;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HiveTableMonitorTasklet extends SimpleTasklet {

    @Autowired
    private HiveTableStats hiveTableStats;

    @Override
    public RepeatStatus execute(ChunkContext chunkContext, StepContribution contribution) throws Exception {
        log.info("hive table stats start=================================>>");
        try {
            hiveTableStats.statistics();
        } catch (Exception e) {
            log.error("Failed to stat hive table info.", e);
            throw e;
        }
        log.info("project hive table stats finish.");
        log.info("hive table stats end <<=================================");
        return RepeatStatus.FINISHED;
    }
}
