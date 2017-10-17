package cn.rongcapital.chorus.monitor.network.test;

import cn.rongcapital.chorus.common.util.SpringBeanUtils;
import cn.rongcapital.chorus.monitor.network.test.util.BasicSpringTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public class NetworkMonitorTest extends BasicSpringTest {

    @Autowired
    private ApplicationContext context;

    @Before
    public void before() {
        SpringBeanUtils.setApplicationContext(context);
    }

    @Test
    public void testMonitor() throws InterruptedException {
        Thread.currentThread().join();
    }

}
