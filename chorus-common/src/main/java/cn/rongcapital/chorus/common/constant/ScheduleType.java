package cn.rongcapital.chorus.common.constant;

import cn.rongcapital.chorus.common.exception.ServiceException;

/**
 * Created by lengyang on 13/04/2017.
 */
public enum ScheduleType {
//    1:一次性;2:周期
    ONCE(1,"一次性"),
    CYCLE(2,"周期"),
    ;
    private final int code;
    private final String desc;

    ScheduleType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ScheduleType getScheduleTypeByCode(int code) {
        for (ScheduleType scheduleType : ScheduleType.values()) {
            if (scheduleType.code == code)
                return scheduleType;
        }
        throw new ServiceException(StatusCode.SCHEDULE_TYPE_ERROR);
    }

    public int getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }
}
