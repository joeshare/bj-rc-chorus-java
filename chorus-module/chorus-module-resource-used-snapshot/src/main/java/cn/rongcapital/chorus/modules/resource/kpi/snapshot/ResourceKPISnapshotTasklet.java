package cn.rongcapital.chorus.modules.resource.kpi.snapshot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by hhlfl on 2017-7-14.
 */
@Slf4j
public class ResourceKPISnapshotTasklet implements Tasklet {
    @Autowired
    private PlatformResourceStats platformResourceStats;
    @Autowired
    private ProjectResourceKPIStats projectResourceKPIStats;

    private int retries;

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        int i=0;
        log.info("resource KPI stats start=================================>>");
        do {
            try {
                if(i>0) log.info(String.format("project resource KPI stats retries(%d)", i));
                projectResourceKPIStats.snapshot();
                log.info("project resource kpi stats finish.");
                platformResourceStats.snapshot();
                log.info("platform resource kpi stats finish.");
                break;
            }catch (Exception ex){
                if(i>=retries) throw ex;
            }
        }while (i++<retries);

        log.info("resource KPI stats end <<=================================");
        return RepeatStatus.FINISHED;
    }
}
