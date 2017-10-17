package cn.rongcapital.chorus.authorization.api.data;

/**
 * Created by shicheng on 2017/4/5.
 */
public enum DWPermissions {

    SELECT("select"),
    UPDATE("update"),
    CREATE("create"),
    DROP("drop"),
    ALTER("alter"),
    INDEX("index"),
    LOCK("lock"),
    ALL("all")
    ;

    DWPermissions(String value) {
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
