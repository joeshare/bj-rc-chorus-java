package cn.rongcapital.chorus.modules.ranger.sync.task;

import cn.rongcapital.chorus.authorization.api.UserDataAuthorization;
import cn.rongcapital.chorus.authorization.api.data.AuthorizationDataDW;
import cn.rongcapital.chorus.authorization.plugin.ranger.RangerUtils;
import cn.rongcapital.chorus.authorization.plugin.ranger.UserDataAuthorizationByRanger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

/**
 * Created by shicheng on 2017/4/11.
 */
@Slf4j
@org.springframework.context.annotation.Configuration
@EnableBatchProcessing
@ComponentScan(basePackages = "cn.rongcapital.chorus.modules.ranger.sync.task")
public class AuthorizationTaskJob {

    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private StepBuilderFactory steps;

    @Bean
    public Job job(@Qualifier(value = "step1") Step step1) {
        return jobs.get("authorizationTaskJob").start(step1).build();
    }

    @Bean
    public Step step1(@Value("${dbHost}") String dbHost,
                      @Value("${dbPort}") Integer dbPort,
                      @Value("${databaseName}") String databaseName,
                      @Value("${dbUserName}") String dbUserName,
                      @Value("${dbPassword}") String dbPassword,
                      @Value("${rangerHost}") String rangerHost,
                      @Value("${rangerLoginUrl}") String rangerLoginUrl,
                      @Value("${rangerRestApiUrl}") String rangerRestApiUrl,
                      @Value("${rangerRepositoryName}") String rangerRepositoryName,
                      @Value("${rangerUsername}") String rangerUsername,
                      @Value("${rangerPassword}") String rangerPassword) {
        return steps.get("authorizationTaskStep1").tasklet((contribution, chunkContext) -> {
            Connection conn = null;
            String sql;
            Statement statement = null;
            UserDataAuthorization authorization = new UserDataAuthorizationByRanger();
            RangerUtils rangerUtils = new RangerUtils();
//                    .rangerLogin(rangerUsername, rangerPassword);

            log.info("database config connection url: jdbc://{}:{}/{}", dbHost, dbPort, databaseName);
            log.info("database connection user: {}", dbUserName);

            String connectUrl = String.format("jdbc:mysql://%s:%s/%s?user=%s&password=%s&useUnicode=true&characterEncoding=UTF8",
                    dbHost, dbPort, databaseName, dbUserName, dbPassword);
            try {
                Class.forName("com.mysql.jdbc.Driver");// 动态加载mysql驱动
                conn = DriverManager.getConnection(connectUrl);
                statement = conn.createStatement();
                sql = "select id, apply_id, apply_code, start_time, end_time, actived from chorus_task where actived = TRUE";
                log.info("execute sql : {}", sql);
                ResultSet set = statement.executeQuery(sql);
                if (set != null) {
                    while (set.next()) {
                        int result = new Date().compareTo(set.getDate("start_time")); // 相等则返回0, new Date()大返回1, 否则返回-1;
                        if (result == 0 || result == 1) {
                            boolean loginStatus = rangerUtils.login(rangerUsername, rangerPassword);
                            if (loginStatus) {
                                AuthorizationDataDW data = new AuthorizationDataDW();
                                data.setEnabled(true);
                                data.setAuthorizationName(set.getString("apply_code"));
                                data.setAuthorizationId(set.getString("apply_id"));
                                boolean flag = authorization.authorizationUpdate(data);
                                if (flag) {
                                    String updateSQL = "update chorus_task set actived = false where id = " + set.getInt("id");
                                    statement.executeUpdate(updateSQL);
                                } else {
                                    log.error("update authorization error, name : ", set.getString("apply_code"));
                                }
                            } else {
                                log.error("ranger login error");
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error("error ", e);
            }finally {
                if (statement != null) {
                    statement.close();
                }
                if (conn != null) {
                    conn.close();
                }
            }
            return RepeatStatus.FINISHED;
        }).build();
    }

}
