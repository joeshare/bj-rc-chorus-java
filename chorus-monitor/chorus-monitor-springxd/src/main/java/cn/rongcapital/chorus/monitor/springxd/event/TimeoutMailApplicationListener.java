package cn.rongcapital.chorus.monitor.springxd.event;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import cn.rongcapital.chorus.das.entity.TaskExecutionTimeout;
import cn.rongcapital.chorus.monitor.springxd.queue.ExecutionTimeoutQueue;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TimeoutMailApplicationListener implements ApplicationListener<TimeoutMailApplicationEvent> {

    @Override
    public void onApplicationEvent(TimeoutMailApplicationEvent event) {
        TaskExecutionTimeout xdExecution = event.getTarget();
        if (xdExecution != null) {
            ExecutionTimeoutQueue.offerTimeout(xdExecution);
        } else {
            log.warn("XDExecution is null for handling.");
        }
    }

}
