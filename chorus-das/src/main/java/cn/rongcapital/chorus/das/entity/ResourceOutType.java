package cn.rongcapital.chorus.das.entity;

/**
 * @author yunzhong
 *
 * @date 2017年9月4日上午10:10:48
 */
public enum ResourceOutType {

    MYSQL("1"), FTP("2");
    private String value;

    ResourceOutType(String value) {
        this.setValue(value);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
