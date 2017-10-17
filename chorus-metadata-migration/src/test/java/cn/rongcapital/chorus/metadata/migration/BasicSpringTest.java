package cn.rongcapital.chorus.metadata.migration;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
@TestPropertySource({ "classpath:application.properties" })
@TestExecutionListeners(listeners = { DependencyInjectionTestExecutionListener.class,
        TransactionalTestExecutionListener.class })
public class BasicSpringTest {
}
