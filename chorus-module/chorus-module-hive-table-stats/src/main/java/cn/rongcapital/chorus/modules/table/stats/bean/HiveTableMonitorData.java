package cn.rongcapital.chorus.modules.table.stats.bean;

import java.util.Date;

import lombok.Data;

/**
 * @author yunzhong
 *
 * @date 2017年8月8日下午5:00:35
 */
@Data
public class HiveTableMonitorData {
    private long id;
    private long projectId;
    private String projectCode;
    private String tableInfoId;
    private String tableName;
    private Date monitorDate;
    private long rows;
    private long storageSize;
}
