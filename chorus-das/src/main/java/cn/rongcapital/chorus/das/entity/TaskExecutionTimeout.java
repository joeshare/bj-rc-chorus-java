package cn.rongcapital.chorus.das.entity;

import java.util.Date;

import lombok.Data;

@Data
public class TaskExecutionTimeout {

    private Integer taskId;
    private Integer taskExecutionId;
    private Date startTime;
    private Date expectEndTime;
    private String status;
    private String email;
    private String error;
    private String jobName;

    private Job job;
}
