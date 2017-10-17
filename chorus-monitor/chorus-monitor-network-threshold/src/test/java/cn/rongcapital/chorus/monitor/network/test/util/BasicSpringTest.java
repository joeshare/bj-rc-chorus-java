package cn.rongcapital.chorus.monitor.network.test.util;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, MailConfig.class, MybatisConfig.class})
@TestPropertySource({"classpath:application.properties"})
public class BasicSpringTest {

}
