package cn.rongcapital.chorus.das.entity;

import java.util.Date;

import lombok.Data;

@Data
public class TableMonitorInfo {
    private Long id;
    private Long projectId;
    private Long tableInfoId;
    private String tableName;
    private Date monitorDate;
    private Long rows;
    private Long storageSize;
}
