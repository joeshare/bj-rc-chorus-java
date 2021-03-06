package cn.rongcapital.chorus.das.entity;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * Created by Athletics on 2017/8/23.
 */
@Data
@ToString
public class ProjectResourceKpiSnapshotVO {

    private Long id;

    private Long projectId;

    private String projectName;

    private Date kpiDate;

    private Integer cpuTotal;

    private Integer cpuUsed;

    private Double cpuUsage;

    private Integer memoryTotal;

    private Integer memoryUsed;

    private Double memoryUsage;

    private Long storageTotal;

    private Long storageUsed;

    private Double storageUsage;

    private Long dataDailyIncr;

    private Integer taskTotal;

    private Integer taskSuccess;

    private Double taskSuccessRate;

    private Byte score;

    private Date createTime;

    private String userName;
}
