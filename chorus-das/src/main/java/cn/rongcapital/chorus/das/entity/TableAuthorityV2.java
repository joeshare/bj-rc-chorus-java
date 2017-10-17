package cn.rongcapital.chorus.das.entity;

import java.util.Date;

public class TableAuthorityV2 {
    private Long tableAuthorityId;

    private String tableInfoId;

    private String columnInfoId;

    private String userId;

    private Date endDate;

    private String tableName;
    private String columnName;
    private Long projectId;

    public Long getTableAuthorityId() {
        return tableAuthorityId;
    }

    public void setTableAuthorityId(Long tableAuthorityId) {
        this.tableAuthorityId = tableAuthorityId;
    }

    public String getTableInfoId() {
        return tableInfoId;
    }

    public void setTableInfoId(String tableInfoId) {
        this.tableInfoId = tableInfoId;
    }

    public String getColumnInfoId() {
        return columnInfoId;
    }

    public void setColumnInfoId(String columnInfoId) {
        this.columnInfoId = columnInfoId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
}
