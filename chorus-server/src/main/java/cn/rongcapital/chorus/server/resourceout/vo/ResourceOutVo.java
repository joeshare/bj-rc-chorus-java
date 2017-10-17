package cn.rongcapital.chorus.server.resourceout.vo;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * 外部资源信息
 *
 * @author shicheng
 * @version 1.0
 * @since <pre>十一月 24, 2016</pre>
 */
@Data
@ToString
public class ResourceOutVo {

    private Long resourceOutId; // 资源 id
    private String host; // 主机或 ip
    private String port = "3306"; // 端口
    private String userName; // 用户名
    private String userPassword; // 用户密码
    private String description; // 资源描述
    private String usage; // 资源用途
    private String type; // 资源类型
    private String userId; // 用户 id
    private String updateUserId; // 更新用户 id
    private String databaseName; // 数据库名称
    private Long projectId; // 项目 ID
    private String resourceName; // 资源名称
    private Date createTime; // 创建时间
    private String createUserName;
    private String path;//type 为 2 FTP时，需要填写的ftp目录路径
    private String connectMode;//ftp连接模式

}
