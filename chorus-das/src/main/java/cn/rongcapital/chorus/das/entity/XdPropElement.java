package cn.rongcapital.chorus.das.entity;

/**
 * t_xd_module表映射BEAN
 *
 * @author lengyang
 */
public class XdPropElement extends CommonEntity {

    /**
     * 主键
     */
    private Integer id;
    /**
     * 主键
     */
    private Integer xdModuleId;
    /**
     * 模块类型 1:Job 2:Stream
     */
    private int xdModuleType;
    /**
     * 元素id
     */
    private String name;
    /**
     * 元素类型
     */
    private String type;
    /**
     * 元素显示名
     */
    private String label;
    /**
     * 是否必须输入项(N:否 Y:是)
     */
    private String inputRequired;
    /**
     * 可描述输入字段预期值的提示信息
     */
    private String placeholder;
    /**
     * 顺序号
     */
    private int sortNum;
    
    private String validateConfig;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getXdModuleId() {
        return xdModuleId;
    }

    public void setXdModuleId(Integer xdModuleId) {
        this.xdModuleId = xdModuleId;
    }

    public int getXdModuleType() {
        return xdModuleType;
    }

    public void setXdModuleType(int xdModuleType) {
        this.xdModuleType = xdModuleType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getInputRequired() {
        return inputRequired;
    }

    public void setInputRequired(String inputRequired) {
        this.inputRequired = inputRequired;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public int getSortNum() {
        return sortNum;
    }

    public void setSortNum(int sortNum) {
        this.sortNum = sortNum;
    }
    
    public String getValidateConfig() {
        return validateConfig;
    }

    public void setValidateConfig(String validateConfig) {
        this.validateConfig = validateConfig;
    }
}
