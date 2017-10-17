package cn.rongcapital.chorus.das.entity;

import java.util.Date;

public class TableAuthority {
    private Long tableAuthorityId;

    private Long tableInfoId;

    private Long columnInfoId;

    private String userId;

    private Date endDate;

    public Long getTableAuthorityId() {
        return tableAuthorityId;
    }

    public void setTableAuthorityId(Long tableAuthorityId) {
        this.tableAuthorityId = tableAuthorityId;
    }

    public Long getTableInfoId() {
        return tableInfoId;
    }

    public void setTableInfoId(Long tableInfoId) {
        this.tableInfoId = tableInfoId;
    }

    public Long getColumnInfoId() {
        return columnInfoId;
    }

    public void setColumnInfoId(Long columnInfoId) {
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
}