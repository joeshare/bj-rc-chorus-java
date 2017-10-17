package cn.rongcapital.chorus.server.metadata.vo;

import lombok.Data;

import java.util.Date;

@Data
public class TableInfoWithProjectNameVo {
    private Long tableInfoId;

    private Long projectId;

    private String tableCode;

    private String tableName;

    private String dataField;

    private String tableType;

    private String isSnapshot;

    private String updateFrequence;

    private String sla;

    private String securityLevel;

    private Byte isOpen;

    private String tableDes;

    private Date createTime;

    private Date updateTime;

    private String statusCode;

    private String projectName;
}
