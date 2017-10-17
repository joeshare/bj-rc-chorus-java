package cn.rongcapital.chorus.monitor.springxd.test.monitor;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import cn.rongcapital.chorus.common.util.SpringBeanUtils;
import cn.rongcapital.chorus.monitor.springxd.SpringXDTimeoutMonitor;
import cn.rongcapital.chorus.monitor.springxd.test.util.BasicSpringTest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SpringXDTimeoutMonitorTest extends BasicSpringTest {

    @Autowired
    private SpringXDTimeoutMonitor timeoutMonitor;
    
    @Autowired
    private ApplicationContext context;
    
    @Before
    public void before(){
        SpringBeanUtils.setApplicationContext(context);
    }

    /**
     * 
     * @author yunzhong
     * @time 2017年6月28日上午9:20:20
     */
    @Test
    public void testmonitorTimeout() {
        log.info("start Test");
        timeoutMonitor.monitorTimeout();
        log.info("end Test");
    }
}
