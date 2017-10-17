package cn.rongcapital.chorus.modules.ranger.audit.stats;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

/**
 * Created by hhlfl on 2017-6-27.
 */
@Slf4j
public class RangerAuditStatsTasklet implements Tasklet {
    private int days;
    private String repos;

    private String dbURL;
    private String userName;
    private String password;

    private String zkHosts;
    private String collectionName;

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public String getRepos() {
        return repos;
    }

    public void setRepos(String repos) {
        this.repos = repos;
    }

    public String getDbURL() {
        return dbURL;
    }

    public void setDbURL(String dbUrl) {
        this.dbURL = dbUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getZkHosts() {
        return zkHosts;
    }

    public void setZkHosts(String zkHosts) {
        this.zkHosts = zkHosts;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        RangerAuditStatsTask rangerAuditStatsTask = new RangerAuditStatsTask(dbURL,userName,password,zkHosts,collectionName);
        rangerAuditStatsTask.doWork(days, repos);
        return RepeatStatus.FINISHED;
    }
}
