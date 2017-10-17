package cn.rongcapital.chorus.authorization.api.data;

/**
 * Created by shicheng on 2017/4/5.
 */
public enum DWTCType {

    INCLUDE("include"),
    EXCLUDE("exclude"),
    ;

    DWTCType(String value) {
        this.value = value;
    }

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
