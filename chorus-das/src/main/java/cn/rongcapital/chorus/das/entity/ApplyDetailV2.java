package cn.rongcapital.chorus.das.entity;

public class ApplyDetailV2 {
    private Long applyDetailId;

    private Long applyFormId;

    private String  tableInfoId;

    private String columnInfoId;

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

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode == null ? null : statusCode.trim();
    }
}
