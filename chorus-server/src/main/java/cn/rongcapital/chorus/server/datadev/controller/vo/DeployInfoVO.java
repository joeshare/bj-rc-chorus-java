package cn.rongcapital.chorus.server.datadev.controller.vo;

import lombok.Data;

@Data
public class DeployInfoVO {
    /**
     * 实例ID
     */
    private Long instanceId;

    /**
     * 告警信息
     */
    private String warningConfig;

    private ScheduleVO schedule;
}
