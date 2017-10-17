package cn.rongcapital.chorus.das.entity;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Li.ZhiWei
 */
@Getter
@Setter
public class XDExecution {
    private int executionId;
    private int instanceId;
    private Date startTime;
    private Date endTime;
    private String status;
    private String jobName;
    private String email;
    private String error;

}
