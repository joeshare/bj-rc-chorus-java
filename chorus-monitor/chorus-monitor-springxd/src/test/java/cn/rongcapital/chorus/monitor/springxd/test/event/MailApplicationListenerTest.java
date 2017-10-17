package cn.rongcapital.chorus.monitor.springxd.test.event;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;

import cn.rongcapital.chorus.common.util.SpringBeanUtils;
import cn.rongcapital.chorus.das.entity.XDExecution;
import cn.rongcapital.chorus.monitor.springxd.event.MailApplicationEvent;
import cn.rongcapital.chorus.monitor.springxd.test.util.BasicSpringTest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MailApplicationListenerTest extends BasicSpringTest {

    @Autowired
    private ApplicationContext context;

    @Before
    public void before() {
        SpringBeanUtils.setApplicationContext(this.context);
    }

    /**
     * 
     * @author yunzhong
     * @version
     * @since 2017年5月22日
     */
    @Test
    public void testSendMessage() {
        XDExecution execution = new XDExecution();
        execution.setEndTime(new Date());
        execution.setExecutionId(1111);
        execution.setInstanceId(1);
        execution.setJobName("chorus_YEoKFkTASbMgaclTrBxE1502278219554");
        execution.setStartTime(new Date());
        execution.setStatus("SENDED");
        ApplicationEvent event = new MailApplicationEvent(execution);
        SpringBeanUtils.publish(event);
        log.info("wait event published.");
    }
}
