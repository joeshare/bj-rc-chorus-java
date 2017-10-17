package cn.rongcapital.chorus.modules.batchjob;

import static org.springframework.xd.module.options.spi.page.PageElementType.InputText;
import static org.springframework.xd.module.options.spi.page.PageElementType.Password;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.xd.module.options.spi.Mixin;
import org.springframework.xd.module.options.spi.ModuleOption;
import org.springframework.xd.module.options.spi.page.PageElement;

/**
 * 批量任务统计定义
 * @author kevin.gong
 * @Time 2017年8月8日 下午2:19:02
 */
@Mixin({})
public class BatchJobStatisticMetadata {

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
     * chorus库连接地址
     */
    private String chorusUrl;

    /**
     * chorus库用户名
     */
    private String chorusUserName;

    /**
     * chorus库密码
     */
    private String chorusPassword; 
    
    /**
     * 重试次数
     */
    private Integer retryCount;

    @PageElement(InputText)
    @Min(0)
    @Max(10)
    @NotNull
    public Integer getRetryCount() {
        return retryCount;
    }

    @ModuleOption(label="重试次数", value = "retry count", defaultValue = "0")
    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }
    
    @PageElement(InputText)
    @NotNull
    public String getXdUrl() {
        return xdUrl;
    }

    @ModuleOption(label="xd库地址", value = "xd connect url")
    public void setXdUrl(String xdUrl) {
        this.xdUrl = xdUrl;
    }

    @PageElement(InputText)
    @NotNull
    public String getXdUserName() {
        return xdUserName;
    }

    @ModuleOption(label="xd库用户名", value = "xd username")
    public void setXdUserName(String xdUserName) {
        this.xdUserName = xdUserName;
    }

    @PageElement(Password)
    @NotNull
    public String getXdPassword() {
        return xdPassword;
    }

    @ModuleOption(label="xd库密码", value = "xd password")
    public void setXdPassword(String xdPassword) {
        this.xdPassword = xdPassword;
    }

    @PageElement(InputText)
    @NotNull
    public String getChorusUrl() {
        return chorusUrl;
    }

    @ModuleOption(label="chorus库地址", value = "chorus connect url")
    public void setChorusUrl(String chorusUrl) {
        this.chorusUrl = chorusUrl;
    }

    @PageElement(InputText)
    @NotNull
    public String getChorusUserName() {
        return chorusUserName;
    }

    @ModuleOption(label="chorus库用户名", value = "chorus username")
    public void setChorusUserName(String chorusUserName) {
        this.chorusUserName = chorusUserName;
    }

    @PageElement(Password)
    @NotNull
    public String getChorusPassword() {
        return chorusPassword;
    }

    @ModuleOption(label="chorus库密码", value = "chorus password")
    public void setChorusPassword(String chorusPassword) {
        this.chorusPassword = chorusPassword;
    }

}
