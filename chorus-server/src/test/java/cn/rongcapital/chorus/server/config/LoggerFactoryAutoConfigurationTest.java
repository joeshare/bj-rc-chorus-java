package cn.rongcapital.chorus.server.config;

import cn.rongcapital.chorus.das.service.LoggerFactory;
import org.junit.Test;
import org.springframework.boot.autoconfigure.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.test.EnvironmentTestUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author yimin
 */
public class LoggerFactoryAutoConfigurationTest {
    private AnnotationConfigApplicationContext context;

    @Test
    public void loggerFactory() throws Exception {
        load();
        LoggerFactory loggerFactory = context.getBean(LoggerFactory.class);
        assertThat(loggerFactory).isNotNull();
        assertThat(loggerFactory.getTableAuditLogger().getName()).isEqualTo(LoggerFactoryAutoConfiguration.TABLE_OPERATIONS_AUDIT);
    }

    @Test
    public void loggerFactoryByCustomTheLoggerName() throws Exception {
        load("logger.name.audit.table=custom-logger-name");
        LoggerFactory loggerFactory = context.getBean(LoggerFactory.class);
        assertThat(loggerFactory).isNotNull();
        assertThat(loggerFactory.getTableAuditLogger().getName()).isEqualTo("custom-logger-name");
    }

    private void load(String... environment) {
        this.context = new AnnotationConfigApplicationContext();
        if (environment != null && environment.length > 0) EnvironmentTestUtils.addEnvironment(this.context, environment);
        this.context.register(LoggerFactoryAutoConfiguration.class, PropertyPlaceholderAutoConfiguration.class);
        this.context.refresh();
    }
}
