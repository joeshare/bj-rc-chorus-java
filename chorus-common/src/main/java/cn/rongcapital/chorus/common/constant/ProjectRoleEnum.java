package cn.rongcapital.chorus.common.constant;

/**
 * Created by alan on 02/05/2017.
 */
public enum ProjectRoleEnum {
    DEV("1"),
    QUERY("2"),
    OWNER("4"),
    ADMIN("5")
    ;
    public final String code;

    ProjectRoleEnum(String code) {
        this.code = code;
    }
    
    public static ProjectRoleEnum getByCode(String code) {
        switch (code) {
        case "1":
            return DEV;
        case "2":
            return ProjectRoleEnum.QUERY;
        case "4":
            return ProjectRoleEnum.OWNER;
        case "5":
            return ProjectRoleEnum.ADMIN;
        default:
            break;
        }
        return null;
    }
}
