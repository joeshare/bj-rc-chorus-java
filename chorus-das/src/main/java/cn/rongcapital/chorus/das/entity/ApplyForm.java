package cn.rongcapital.chorus.das.entity;

import java.util.Date;

public class ApplyForm {
    private Long applyFormId;

    private Long tableInfoId;

    private Date endDate;

    private String applyUserId;

    private Date applyTime;

    private String reason;

    private String dealUserId;

    private String dealInstruction;

    private Date dealTime;

    private String statusCode;

    private String applyUserName;

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

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getApplyUserId() {
        return applyUserId;
    }

    public void setApplyUserId(String applyUserId) {
        this.applyUserId = applyUserId == null ? null : applyUserId.trim();
    }

    public Date getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(Date applyTime) {
        this.applyTime = applyTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason == null ? null : reason.trim();
    }

    public String getDealUserId() {
        return dealUserId;
    }

    public void setDealUserId(String dealUserId) {
        this.dealUserId = dealUserId == null ? null : dealUserId.trim();
    }

    public String getDealInstruction() {
        return dealInstruction;
    }

    public void setDealInstruction(String dealInstruction) {
        this.dealInstruction = dealInstruction == null ? null : dealInstruction.trim();
    }

    public Date getDealTime() {
        return dealTime;
    }

    public void setDealTime(Date dealTime) {
        this.dealTime = dealTime;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode == null ? null : statusCode.trim();
    }

    public String getApplyUserName() {
        return applyUserName;
    }

    public void setApplyUserName(String applyUserName) {
        this.applyUserName = applyUserName == null ? null : applyUserName.trim();
    }
}