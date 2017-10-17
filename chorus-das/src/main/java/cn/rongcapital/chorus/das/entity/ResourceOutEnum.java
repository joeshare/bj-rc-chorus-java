package cn.rongcapital.chorus.das.entity;

/**
 * Created by Athletics on 2017/9/6.
 */
public enum  ResourceOutEnum {
    MYSQL("1", "mysql"),
    FTP("2", "ftp"),
    SFTP("3", "sftp")
            ;

    private String code;
    private String name;
    ResourceOutEnum(String code, String name){
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
