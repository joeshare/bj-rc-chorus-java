package cn.rongcapital.chorus.server.config;

import cn.rongcapital.chorus.das.service.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yimin
 */
@Configuration
public class LoggerFactoryAutoConfiguration {
    public static final String TABLE_OPERATIONS_AUDIT = "table-operations-audit";

    @Bean
    public LoggerFactory loggerFactory(@Value("${logger.name.audit.table:" + TABLE_OPERATIONS_AUDIT + "}") String loggerName) {
        return () -> org.slf4j.LoggerFactory.getLogger(loggerName);
    }
}
