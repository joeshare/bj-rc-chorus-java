package cn.rongcapital.chorus.das.entity;

public class ApplyDetail {
    private Long applyDetailId;

    private Long applyFormId;

    private Long tableInfoId;

    private Long columnInfoId;

    private String statusCode;

    public Long getApplyDetailId() {
        return applyDetailId;
    }

    public void setApplyDetailId(Long applyDetailId) {
        this.applyDetailId = applyDetailId;
    }

    public Long getApplyFormId() {
        return applyFormId;
    }

    public void setApplyFormId(Long applyFormId) {
        this.applyFormId = applyFormId;
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

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode == null ? null : statusCode.trim();
    }
}