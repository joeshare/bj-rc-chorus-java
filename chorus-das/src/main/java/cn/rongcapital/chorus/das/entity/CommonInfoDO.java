package cn.rongcapital.chorus.das.entity;

import java.util.Date;

/**
 * 通用信息类
 * @author kevin.gong
 * @Time 2017年9月19日 上午10:29:37
 */
public class CommonInfoDO {
    
    private int id;

    private String userId;
    
    private String attributeId;
    
    private String value;
    
    private Date updateTime;
    
    private Date createTime;
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(String attributeId) {
        this.attributeId = attributeId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    
}
