package cn.rongcapital.chorus.test.monitor.ranger;

import cn.rongcapital.chorus.common.util.SpringBeanUtils;
import cn.rongcapital.chorus.monitor.ranger.RangerAuditMonitor;
import cn.rongcapital.chorus.test.monitor.ranger.util.BasicSpringTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public class RangerAuditMonitorTest extends BasicSpringTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private RangerAuditMonitor auditMonitor;

    @Before
    public void before() {
        SpringBeanUtils.setApplicationContext(applicationContext);
    }

    @Test
    public void testTask() throws InterruptedException {
        Thread.currentThread().join();
    }

}
