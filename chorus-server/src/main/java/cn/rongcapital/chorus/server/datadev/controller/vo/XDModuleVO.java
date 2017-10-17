package cn.rongcapital.chorus.server.datadev.controller.vo;

import lombok.Data;

import java.security.Timestamp;
import java.util.Date;

@Data
public class XDModuleVO {
    /**
     * 主键
     */
    private Integer moduleId;

    private int moduleType;

    private String xdModuleTypeName;

    /**
     * Spring XD 定义Module名称
     */
    private String moduleName;

    /**
     * Spring XD 定义Module画面显示别名
     */
    private String moduleAliasName;

    /**
     * 描述
     */
    private String remark;


    /*add by hhl for module upload.*/
    /***
     * 组件级别，0：平台，1：项目
     */
    private int moduleLevel;
    /***
     * 项目ID
     */
    private long projectId;
    /**
     * 组件类别 0：批量，1：流式
     */
    private int moduleCategory;

    /***
     * 前台显示名称
     */
    private String moduleViewName;

    /***
     * 创建用户名
     */
    private String createUserName;

    /***
     * 更新用户名
     */
    private String updateUserName;

    /**
     * 更新时间
     */
    private Date updateTime;

    /***
     * 创建时间
     *
     */
    private Date createTime;

    private String projectName;

}
