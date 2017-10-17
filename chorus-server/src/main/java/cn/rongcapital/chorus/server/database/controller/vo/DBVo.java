package cn.rongcapital.chorus.server.database.controller.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by shicheng on 2016/11/24.
 */
@Data
@NoArgsConstructor
public class DBVo {

    private String host;
    private String port;
    private String userName;
    private String password;
    private String databaseName;
    private String url;
    private String path;
    private String type;

    public DBVo(String url, String userName, String password, String databaseName) {
        this.url = url;
        this.userName = userName;
        this.password = password;
        this.databaseName = databaseName;
    }
//    private String encoding;
//    private String host;

}
