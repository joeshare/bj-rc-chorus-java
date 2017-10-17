package cn.rongcapital.chorus.das.entity;


/**
 *  数据类型字典实体类
 */
public class DatatypeElement {
    private Integer id;         //主键ID

    private String typeCode;    //类型CODE

    private String typeName;    //类型名称

    private Integer status;     //数据状态，0-有效，1-无效

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode == null ? null : typeCode.trim();
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName == null ? null : typeName.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}