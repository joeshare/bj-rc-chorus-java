package cn.rongcapital.chorus.datalab.service;

/**
 * Created by abiton on 16/03/2017.
 */
public enum DatalabStatus {
    START("1","已启动"),
    STOP("2","已停止"),
    ;
    private final String code;
    private final String desc;

    DatalabStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }
}
