package cn.rongcapital.chorus.das.ha;

import cn.rongcapital.chorus.das.entity.HostInfo;
import cn.rongcapital.chorus.das.util.TestConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DBTestConfig.class})
@TestPropertySource({"classpath:application.properties"})
@TestExecutionListeners(listeners = {DependencyInjectionTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
public class MySQLHATest {

    @Autowired
    private HaService service;

    @Test
    public void testSelect() {
        for (int i = 0; i < 10; i++) {
            List<HostInfo> hosts = service.select();
            Assert.assertNotNull(hosts);
            System.out.println(hosts.size());
        }
    }

    @Test
    public void testInsert() {
        for (int i = 11; i < 12; i++) {
            service.insert(i);
        }
    }
}
