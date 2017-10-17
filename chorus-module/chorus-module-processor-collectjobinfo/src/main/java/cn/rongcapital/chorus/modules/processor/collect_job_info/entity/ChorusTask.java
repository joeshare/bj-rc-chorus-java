package cn.rongcapital.chorus.modules.processor.collect_job_info.entity;

import lombok.Data;

/**
 * Created by abiton on 10/08/2017.
 */
@Data
public class ChorusTask {
    private Integer taskId;
    private Integer jobId;
    private String moduleName;
    private String taskName;
    private String aliasName;
}
