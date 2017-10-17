package cn.rongcapital.chorus.modules.resource.kpi.snapshot.bean;

import lombok.Data;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Created by hhlfl on 2017-7-20.
 */
@Data
public class PlatformResourceKPISnapshot {
    private long id;
    private int cpuTotal;
    private int cpuUsed;
    private int memoryTotal;
    private int memoryUsed;
    private long storageTotal;
    private long storageUsed;
    private Date snapshotDate;
    private Timestamp createTime;
}
