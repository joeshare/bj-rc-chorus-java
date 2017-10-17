package cn.rongcapital.chorus.das.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ColumnInfoV2  implements Serializable{
    private static final long serialVersionUID = 8639515124319773811L;

    private String columnInfoId;

    private String tableInfoId;

    private String columnName;

    private String columnDesc;

    private String columnType;

    private String columnLength;

    private String columnPrecision;

    private String securityLevel;

    private Byte isKey;

    private Byte isRefKey;

    private Byte isIndex;

    private Byte isNull;

    private Byte isPartitionKey;

    private Date createTime;

    private Date updateTime;

    private Integer columnOrder;//用于记录列顺序

    private String statusCode;

    public String getColumnInfoId() {
        return columnInfoId;
    }

    public void setColumnInfoId(String columnInfoId) {
        this.columnInfoId = columnInfoId;
    }

    public String getTableInfoId() {
        return tableInfoId;
    }

    public void setTableInfoId(String tableInfoId) {
        this.tableInfoId = tableInfoId;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName == null ? null : columnName.trim();
    }

    public String getColumnDesc() {
        return columnDesc;
    }

    public void setColumnDesc(String columnDesc) {
        this.columnDesc = columnDesc == null ? null : columnDesc.trim();
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType == null ? null : columnType.trim();
    }

    public String getColumnLength() {
        return columnLength;
    }

    public void setColumnLength(String columnLength) {
        this.columnLength = columnLength == null ? null : columnLength.trim();
    }

    public String getColumnPrecision() {
        return columnPrecision;
    }

    public void setColumnPrecision(String columnPrecision) {
        this.columnPrecision = columnPrecision == null ? null : columnPrecision.trim();
    }

    public String getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(String securityLevel) {
        this.securityLevel = securityLevel == null ? null : securityLevel.trim();
    }

    public Byte getIsKey() {
        return isKey;
    }

    public void setIsKey(Byte isKey) {
        this.isKey = isKey;
    }

    public Byte getIsRefKey() {
        return isRefKey;
    }

    public void setIsRefKey(Byte isRefKey) {
        this.isRefKey = isRefKey;
    }

    public Byte getIsIndex() {
        return isIndex;
    }

    public void setIsIndex(Byte isIndex) {
        this.isIndex = isIndex;
    }

    public Byte getIsNull() {
        return isNull;
    }

    public void setIsNull(Byte isNull) {
        this.isNull = isNull;
    }

    public Byte getIsPartitionKey() {
        return isPartitionKey;
    }

    public void setIsPartitionKey(Byte isPartitionKey) {
        this.isPartitionKey = isPartitionKey;
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

    public Integer getColumnOrder() {
        return columnOrder;
    }

    public void setColumnOrder(Integer columnOrder) {
        this.columnOrder = columnOrder;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode == null ? null : statusCode.trim();
    }
}
