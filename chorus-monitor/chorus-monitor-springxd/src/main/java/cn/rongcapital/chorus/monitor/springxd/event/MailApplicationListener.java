package cn.rongcapital.chorus.monitor.springxd.event;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import cn.rongcapital.chorus.das.entity.XDExecution;
import cn.rongcapital.chorus.monitor.springxd.queue.ExecutionAlertQueue;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MailApplicationListener implements ApplicationListener<MailApplicationEvent> {

    @Override
    public void onApplicationEvent(MailApplicationEvent event) {
        XDExecution xdExecution = event.getTarget();
        if (xdExecution != null) {
            ExecutionAlertQueue.offerTimeout(xdExecution);
        } else {
            log.warn("XDExecution is null for handling.");
        }
    }

}
