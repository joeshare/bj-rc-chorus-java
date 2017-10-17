package cn.rongcapital.chorus.das.entity;

/**
 * application信息
 * @author kevin.gong
 * @Time 2017年9月26日 下午3:21:42
 */
public class ApplicationInfo {

    private Integer appId;
    
    private Long startTime;

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }
}
