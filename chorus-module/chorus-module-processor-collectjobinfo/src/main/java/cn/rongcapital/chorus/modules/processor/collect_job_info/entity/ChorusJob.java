package cn.rongcapital.chorus.modules.processor.collect_job_info.entity;

import lombok.Data;

/**
 * Created by abiton on 10/08/2017.
 */
@Data
public class ChorusJob {
    private int jobId;
    private int projectId;
    private String jobName;
    private String jobAliasName;
    private String userName;
}
