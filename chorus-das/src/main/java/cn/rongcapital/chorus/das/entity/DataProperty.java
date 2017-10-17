package cn.rongcapital.chorus.das.entity;

import java.util.Date;

/**
 * 数据属性实体
 */
public class DataProperty {
    private Integer id;             //主键ID

    private String propertyName;    //属性名称

    private String propertyCode;    //属性CODE

    private String propertyDatatype;//属性数据类型

    private Integer propertyLength; //属性长度

    private String propertyDesc;    //属性描述

    private Integer dataId;         //关联数据ID

    private String createUser;      //创建人

    private Date createTime;        //创建时间

    private Integer status;         //数据状态，0-有效，1-无效

    private String securityLevel;   //安全等级

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName == null ? null : propertyName.trim();
    }

    public String getPropertyCode() {
        return propertyCode;
    }

    public void setPropertyCode(String propertyCode) {
        this.propertyCode = propertyCode;
    }

    public String getPropertyDatatype() {
        return propertyDatatype;
    }

    public void setPropertyDatatype(String propertyDatatype) {
        this.propertyDatatype = propertyDatatype == null ? null : propertyDatatype.trim();
    }

    public Integer getPropertyLength() {
        return propertyLength;
    }

    public void setPropertyLength(Integer propertyLength) {
        this.propertyLength = propertyLength;
    }

    public String getPropertyDesc() {
        return propertyDesc;
    }

    public void setPropertyDesc(String propertyDesc) {
        this.propertyDesc = propertyDesc == null ? null : propertyDesc.trim();
    }

    public Integer getDataId() {
        return dataId;
    }

    public void setDataId(Integer dataId) {
        this.dataId = dataId;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser == null ? null : createUser.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(String securityLevel) {
        this.securityLevel = securityLevel;
    }
}