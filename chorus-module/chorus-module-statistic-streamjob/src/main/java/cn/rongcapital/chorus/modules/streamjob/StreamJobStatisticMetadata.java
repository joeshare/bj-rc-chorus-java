package cn.rongcapital.chorus.modules.streamjob;

import static org.springframework.xd.module.options.spi.page.PageElementType.InputText;
import static org.springframework.xd.module.options.spi.page.PageElementType.Password;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.xd.module.options.spi.Mixin;
import org.springframework.xd.module.options.spi.ModuleOption;
import org.springframework.xd.module.options.spi.page.PageElement;

/**
 * 流式任务统计MetaData
 * @author kevin.gong
 * @Time 2017年8月9日 上午9:45:28
 */
@Mixin({})
public class StreamJobStatisticMetadata {

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
     * 监控spring xd zookeeper地址
     */
    private String monitorSpringXdZkPath;

    /**
     * zookeeper地址
     */
    private String zookeeperAddress;

    /**
     * zookeeper超时时间
     */
    private Integer zookeeperTimeout;
    
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
    public String getMonitorSpringXdZkPath() {
        return monitorSpringXdZkPath;
    }

    @ModuleOption(label="zk存储xd监控路径", value = "monitor spring xd zookeeper path", defaultValue = "/xd")
    public void setMonitorSpringXdZkPath(String monitorSpringXdZkPath) {
        this.monitorSpringXdZkPath = monitorSpringXdZkPath;
    }

    @PageElement(InputText)
    @NotNull
    public String getZookeeperAddress() {
        return zookeeperAddress;
    }

    @ModuleOption(label="zookeeper地址", value = "zookeeper address")
    public void setZookeeperAddress(String zookeeperAddress) {
        this.zookeeperAddress = zookeeperAddress;
    }

    @PageElement(InputText)
    @NotNull
    public Integer getZookeeperTimeout() {
        return zookeeperTimeout;
    }

    @ModuleOption(label="zookeeper超时时间", value = "zookeeper time out", defaultValue = "30000")
    public void setZookeeperTimeout(Integer zookeeperTimeout) {
        this.zookeeperTimeout = zookeeperTimeout;
    }

    @PageElement(InputText)
    @NotNull
    public String getChorusUrl() {
        return chorusUrl;
    }

    @ModuleOption(label="chorus地址", value = "chorus connect url")
    public void setChorusUrl(String chorusUrl) {
        this.chorusUrl = chorusUrl;
    }

    @PageElement(InputText)
    @NotNull
    public String getChorusUserName() {
        return chorusUserName;
    }

    @ModuleOption(label="chorus用户名", value = "chorus username")
    public void setChorusUserName(String chorusUserName) {
        this.chorusUserName = chorusUserName;
    }

    @PageElement(Password)
    @NotNull
    public String getChorusPassword() {
        return chorusPassword;
    }

    @ModuleOption(label="chorus密码", value = "chorus password")
    public void setChorusPassword(String chorusPassword) {
        this.chorusPassword = chorusPassword;
    }
}
