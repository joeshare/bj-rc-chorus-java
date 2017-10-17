package cn.rongcapital.chorus.modules.resource.kpi.snapshot;

import org.springframework.xd.module.options.spi.ModuleOption;
import org.springframework.xd.module.options.spi.page.PageElement;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import static org.springframework.xd.module.options.spi.page.PageElementType.InputText;
import static org.springframework.xd.module.options.spi.page.PageElementType.Password;

/**
 * Created by hhlfl on 2017-7-14.
 */
public class ResourceKPISnapshotMetadata {
    //chorus DB URL
    private String chorusDBURL;
    //用户名称
    private String userName;
    //用户密码
    private String password;
    //xd URL
    private String xdDBURL;
    //xd db 用户名称
    private String xdUserName;
    //xd 密码
    private String xdPassword;

    private int retries;

    @PageElement(InputText)
    public String getChorusDBURL() {
        return chorusDBURL;
    }

    @ModuleOption(value="chorus mysql db url.",defaultValue = "\"jdbc:mysql://{ip}:{port}/{db}?useUnicode=true&characterEncoding=utf-8&useSSL=false\"")
    public void setChorusDBURL(String chorusDBURL) {
        this.chorusDBURL = chorusDBURL;
    }

    @PageElement(InputText)
    public String getUserName() {
        return userName;
    }

    @ModuleOption(value="chours db user name")
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @PageElement(Password)
    public String getPassword() {
        return password;
    }

    @ModuleOption(value = "chorus db password")
    public void setPassword(String password) {
        this.password = password;
    }

    @PageElement(InputText)
    public String getXdDBURL() {
        return xdDBURL;
    }

    @ModuleOption(value = "xd db url", defaultValue="\"jdbc:mysql://{ip}:{port}/{db}?useUnicode=true&characterEncoding=utf-8&useSSL=false\"")
    public void setXdDBURL(String xdDBURL) {
        this.xdDBURL = xdDBURL;
    }
    @PageElement(InputText)
    public String getXdUserName() {
        return xdUserName;
    }

    @ModuleOption(value = "xd db user name")
    public void setXdUserName(String xdUserName) {
        this.xdUserName = xdUserName;
    }
    @PageElement(Password)
    public String getXdPassword() {
        return xdPassword;
    }

    @ModuleOption(value = "xd db password")
    public void setXdPassword(String xdPassword) {
        this.xdPassword = xdPassword;
    }


    @PageElement(InputText)
    @Max(10)
    @Min(0)
    public int getRetries() {
        return retries;
    }

    @ModuleOption(value="number of retries.")
    public void setRetries(int retries) {
        this.retries = retries;
    }
}
