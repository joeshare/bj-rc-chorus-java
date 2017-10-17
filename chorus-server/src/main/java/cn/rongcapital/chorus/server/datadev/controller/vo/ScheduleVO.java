package cn.rongcapital.chorus.server.datadev.controller.vo;

import cn.rongcapital.chorus.common.constant.ScheduleType;
import lombok.Data;

@Data
public class ScheduleVO {
    /**
     * 主键
     */
    private Integer scheduleId;
    /**
     * 任务类型(1:一次性;2:周期)
     */
    private Integer scheduleType;
    /**
     * 任务类型(1:一次性;2:周期)
     */
    private String labScheduleType;

    /**
     * CRON表达式
     */
    private String cronExpression;

    public void setScheduleType(ScheduleType type){
        this.scheduleType = type.getCode();
        this.labScheduleType = type.getDesc();
    }
}
