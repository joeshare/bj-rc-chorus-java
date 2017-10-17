package cn.rongcapital.chorus.metadata.migration;

import cn.rongcapital.chorus.common.util.SpringBeanUtils;
import cn.rongcapital.chorus.common.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Created by hhlfl on 2017-7-25.
 */
@Slf4j
@Component
public class MigrateMain implements CommandLineRunner{
    @Autowired
    private DatabaseEntityMigration databaseEntityMigration;
    @Autowired
    private TableAndColumnEntityMigration tableAndColumnEntityMigration;

    @Override
    public void run(String... strings) throws Exception {
        try{
            log.info("=================================>metadata migration start");
            databaseEntityMigration.migrate();
            log.info("database entity migration finish.");
            tableAndColumnEntityMigration.migrate();
            log.info("table and column entity migration finish");
        }catch (Exception ex){
            log.error("metadata migration failed.",ex);
        }

        log.info("metadata migration end<======================================");
    }
}
