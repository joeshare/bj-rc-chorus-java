package cn.rongcapital.chorus.common.constant;

import cn.rongcapital.chorus.common.exception.ServiceException;

/**
 * Created by lengyang on 13/04/2017.
 */
public enum JobType {
    //任务类型(1:实时 2:定期)
    ONCE(1,"实时"),
    CYCLE(2,"定期"),
    ;
    private final int code;
    private final String desc;

    JobType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static JobType getJobTypeByCode(int code) {
        for (JobType scheduleType : JobType.values()) {
            if (scheduleType.code == code)
                return scheduleType;
        }
        throw new ServiceException(StatusCode.DATA_DEV_JOB_TYPE_ERROR);
    }

    public int getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }
}
