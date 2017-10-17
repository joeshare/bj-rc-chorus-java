package cn.rongcapital.chorus.metadata.migration;

import cn.rongcapital.chorus.metadata.migration.config.ApplicationConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Created by hhlfl on 2017-7-26.
 */

public class MetaDataMigrationApp {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationConfig.class, args);

    }
}
