package cn.chorus.module.xd.job.status.sync.entity;

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

}
