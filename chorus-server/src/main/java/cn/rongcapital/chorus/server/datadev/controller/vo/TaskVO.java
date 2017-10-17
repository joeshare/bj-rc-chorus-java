package cn.rongcapital.chorus.server.datadev.controller.vo;

import lombok.Data;

@Data
public class TaskVO {
    /**
     * 业务主键
     */
    private Integer taskId;

    /**
     * 步骤名（英文）
     */
    private String taskName;

    /**
     * 模块类型 1:默认Job 2:用户自定义Job 3:默认Stream source 4:用户自定义Stream source 5:默认Stream processor 6:用自定义Stream processor 7:默认Stream sink 8:用户自定义Stream sink 9:默认Stream other 10:用户自定义Stream other 11:job definition
     */
    private int moduleType;

    /**
     * Srping XD定义模块名
     */
    private String moduleName;

    /**
     * Step taskDSL
     */
    private String taskDSL;

    /**
     * 变量
     */
    private String variable;
}
