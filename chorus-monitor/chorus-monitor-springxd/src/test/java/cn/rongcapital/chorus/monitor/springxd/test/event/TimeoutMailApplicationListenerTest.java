package cn.rongcapital.chorus.monitor.springxd.test.event;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;

import cn.rongcapital.chorus.common.util.SpringBeanUtils;
import cn.rongcapital.chorus.das.entity.Job;
import cn.rongcapital.chorus.das.entity.TaskExecutionTimeout;
import cn.rongcapital.chorus.monitor.springxd.event.TimeoutMailApplicationEvent;
import cn.rongcapital.chorus.monitor.springxd.test.util.BasicSpringTest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TimeoutMailApplicationListenerTest extends BasicSpringTest {

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
        TaskExecutionTimeout timeout = new TaskExecutionTimeout();
        timeout.setExpectEndTime(new Date());
        Job job = new Job();
        job.setJobId(475);
        job.setCreateUserName("wangyunzhong");
        job.setProjectId(222657);
        job.setJobName("chorus_twbBqqaKPazakvrxnHQV1498467184097");
        job.setJobAliasName("dwsqltest");
        timeout.setJob(job );
        timeout.setStartTime(new Date());
        timeout.setTaskExecutionId(100);
        timeout.setTaskId(1);
        ApplicationEvent event = new TimeoutMailApplicationEvent(timeout );
        SpringBeanUtils.publish(event);
        log.info("wait event published.");
    }
}
