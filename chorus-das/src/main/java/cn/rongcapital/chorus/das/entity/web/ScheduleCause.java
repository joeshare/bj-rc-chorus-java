package cn.rongcapital.chorus.das.entity.web;

public class ScheduleCause extends CommonCause {

    /**
     * CRON表达式
     */
    private String cronExpression;

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }
}
