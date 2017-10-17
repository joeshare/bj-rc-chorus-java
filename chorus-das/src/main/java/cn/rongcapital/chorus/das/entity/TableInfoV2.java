package cn.rongcapital.chorus.das.entity;

import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TableInfoV2 implements Serializable {
    private static final long serialVersionUID = 4299530964421011984L;

    private String tableInfoId;

    private Long projectId;

    @Getter
    @Setter
    private String projectCode;

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

    private String createUser;

    private String createUserId;

    private List<HashMap<String, Object>> columns;

    public String  getTableInfoId() {
        return tableInfoId;
    }

    public void setTableInfoId(String tableInfoId) {
        this.tableInfoId = tableInfoId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getTableCode() {
        return tableCode;
    }

    public void setTableCode(String tableCode) {
        this.tableCode = tableCode == null ? null : tableCode.trim();
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName == null ? null : tableName.trim();
    }

    public String getDataField() {
        return dataField;
    }

    public void setDataField(String dataField) {
        this.dataField = dataField == null ? null : dataField.trim();
    }

    public String getTableType() {
        return tableType;
    }

    public void setTableType(String tableType) {
        this.tableType = tableType == null ? null : tableType.trim();
    }

    public String getIsSnapshot() {
        return isSnapshot;
    }

    public void setIsSnapshot(String isSnapshot) {
        this.isSnapshot = isSnapshot == null ? null : isSnapshot.trim();
    }

    public String getUpdateFrequence() {
        return updateFrequence;
    }

    public void setUpdateFrequence(String updateFrequence) {
        this.updateFrequence = updateFrequence == null ? null : updateFrequence.trim();
    }

    public String getSla() {
        return sla;
    }

    public void setSla(String sla) {
        this.sla = sla == null ? null : sla.trim();
    }

    public String getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(String securityLevel) {
        this.securityLevel = securityLevel == null ? null : securityLevel.trim();
    }

    public Byte getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(Byte isOpen) {
        this.isOpen = isOpen;
    }

    public String getTableDes() {
        return tableDes;
    }

    public void setTableDes(String tableDes) {
        this.tableDes = tableDes == null ? null : tableDes.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode == null ? null : statusCode.trim();
    }

    public List<HashMap<String, Object>> getColumns() {
        return columns;
    }

    public void setColumns(List<HashMap<String, Object>> columns) {
        this.columns = columns;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }
}
