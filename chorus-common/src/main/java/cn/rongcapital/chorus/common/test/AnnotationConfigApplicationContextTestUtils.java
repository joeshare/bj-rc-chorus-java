package cn.rongcapital.chorus.common.test;

import org.springframework.boot.test.EnvironmentTestUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author yimin
 */
public class AnnotationConfigApplicationContextTestUtils {

    public static ApplicationContext applicationContextLauncher(Class[] annotatedClasses, String... environmentPairs) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(annotatedClasses);
        EnvironmentTestUtils.addEnvironment(applicationContext, environmentPairs);
        applicationContext.refresh();
        return applicationContext;

    }
}
