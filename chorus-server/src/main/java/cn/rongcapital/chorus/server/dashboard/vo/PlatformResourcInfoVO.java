package cn.rongcapital.chorus.server.dashboard.vo;

import lombok.Data;

/**
 * Created by Athletics on 2017/7/19.
 */
@Data
public class PlatformResourcInfoVO {

    private String date;

    private String cpuUseRate;

    private String memoryUseRate;

    private String storageUseRate;

    private String dataDailyIncrTotal;

    private String taskSuccessRate;
}
