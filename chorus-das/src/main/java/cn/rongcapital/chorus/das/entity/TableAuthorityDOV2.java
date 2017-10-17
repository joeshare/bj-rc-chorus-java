package cn.rongcapital.chorus.das.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by fuxiangli on 2016-11-30.
 */
@Data
public class TableAuthorityDOV2 implements Serializable{
    private static final long serialVersionUID = 7773739028700671031L;

    private Long projectId;

    private String projectCode;

    private String projectName;

    private String tableInfoId;

    private String tableName;

    private String columnInfoId;

    private String columnName;

    private String userId;

    private Date endDate;
}
