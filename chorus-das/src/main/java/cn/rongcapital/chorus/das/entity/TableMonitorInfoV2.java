package cn.rongcapital.chorus.das.entity;

import lombok.Data;

import java.util.Date;

@Data
public class TableMonitorInfoV2 {
    private Long id;
    private Long projectId;
    private String  tableInfoId;
    private String tableName;
    private Date monitorDate;
    private Long rows;
    private Long storageSize;
}
