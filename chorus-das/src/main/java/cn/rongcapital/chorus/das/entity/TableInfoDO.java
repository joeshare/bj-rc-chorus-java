package cn.rongcapital.chorus.das.entity;

import lombok.Data;

/**
 * Created by fuxiangli on 2016-11-23.
 */
@Data
public class TableInfoDO {
    private Long tableInfoId;

    private Long projectId;

    private String projectCode;

    private String projectName;

    private String tableName;

    private String securityLevel;

    private String tableDes;
}
