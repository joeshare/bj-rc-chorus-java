package cn.chorus.module.xd.job.status.sync;

import static org.springframework.xd.module.options.spi.page.PageElementType.InputText;
import static org.springframework.xd.module.options.spi.page.PageElementType.Password;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.NumberFormat;
import org.springframework.xd.module.options.spi.ModuleOption;
import org.springframework.xd.module.options.spi.page.PageElement;

/**
 * @author Lovett
 */
public class XdJobStatusSyncMetadata {
    /**
     * xd库连接地址
     */
    private String xdUrl;

    /**
     * xd库用户名
     */
    private String xdUserName;

    /**
     * xd库密码
     */
    private String xdPassword;

    /**
     * 重试次数
     */
    private Integer retryCount;
    /**
     * 任务超过时长后会被更新
     */
    private Integer timeOutHours;

    @PageElement(InputText)
    @Min(0)
    @Max(10)
    @NotNull
    public Integer getRetryCount() {
        return retryCount;
    }

    @ModuleOption(label = "重试次数", value = "retry count", defaultValue = "0")
    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    @PageElement(InputText)
    @NotNull
    public String getXdUrl() {
        return xdUrl;
    }

    @ModuleOption(label = "xd库地址", value = "xd connect url")
    public void setXdUrl(String xdUrl) {
        this.xdUrl = xdUrl;
    }

    @PageElement(InputText)
    @NotNull
    public String getXdUserName() {
        return xdUserName;
    }

    @ModuleOption(label = "xd库用户名", value = "xd username")
    public void setXdUserName(String xdUserName) {
        this.xdUserName = xdUserName;
    }

    @PageElement(Password)
    @NotNull
    public String getXdPassword() {
        return xdPassword;
    }

    @ModuleOption(label = "xd库密码", value = "xd password")
    public void setXdPassword(String xdPassword) {
        this.xdPassword = xdPassword;
    }

    @PageElement(InputText)
    @NotNull
    @NumberFormat
    public Integer getTimeOutHours() {
        return timeOutHours;
    }

    @ModuleOption(label = "任务超过指定时长（小时）会被更新状态", value = "timeout hours")
    public void setTimeOutHours(Integer timeOutHours) {
        this.timeOutHours = timeOutHours;
    }

}
