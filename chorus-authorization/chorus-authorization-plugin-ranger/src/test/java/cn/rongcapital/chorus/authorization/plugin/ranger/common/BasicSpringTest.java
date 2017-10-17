package cn.rongcapital.chorus.authorization.plugin.ranger.common;

import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.PropertyPlaceholderAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class, PropertyPlaceholderAutoConfiguration.class })
@TestPropertySource({ "classpath:application.properties" })
public class BasicSpringTest {

}
