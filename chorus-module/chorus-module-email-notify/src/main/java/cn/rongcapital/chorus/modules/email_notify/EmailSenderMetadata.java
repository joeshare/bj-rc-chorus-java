package cn.rongcapital.chorus.modules.email_notify;

import org.springframework.xd.module.options.spi.Mixin;
import org.springframework.xd.module.options.spi.ModuleOption;
import org.springframework.xd.module.options.spi.page.PageElement;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import static org.springframework.xd.module.options.spi.page.PageElementType.InputText;
import static org.springframework.xd.module.options.spi.page.PageElementType.Password;
import static org.springframework.xd.module.options.spi.page.PageElementType.TextArea;;

@Mixin({})
public class EmailSenderMetadata {
    // chorus DB URL
    private String chorusURL;
    // 用户名称
    private String chorusUser;
    // 用户密码
    private String chorusPassword;

    private String emailContent;
    private String title;

    private int retries;

    @PageElement(InputText)
    @NotNull
    public String getChorusURL() {
        return chorusURL;
    }

    @ModuleOption(label = "chorus数据库URL", value = "chorus mysql db url.", defaultValue = "\"jdbc:mysql://{ip}:{port}/{db}?useUnicode=true&characterEncoding=utf-8&useSSL=false\"")
    public void setChorusURL(String chorusURL) {
        this.chorusURL = chorusURL;
    }

    @PageElement(InputText)
    @NotNull
    public String getChorusUser() {
        return chorusUser;
    }

    @ModuleOption(label = "chorus数据库用户名",value = "chours db user name")
    public void setChorusUser(String chorusUser) {
        this.chorusUser = chorusUser;
    }

    @PageElement(Password)
    @NotNull
    public String getChorusPassword() {
        return chorusPassword;
    }

    @ModuleOption(label = "chorus数据库密码",value = "chorus db password")
    public void setChorusPassword(String chorusPassword) {
        this.chorusPassword = chorusPassword;
    }

    @PageElement(InputText)
    @Max(10)
    @Min(0)
    public int getRetries() {
        return retries;
    }

    @ModuleOption(label = "重试次数",value = "number of retries.")
    public void setRetries(int retries) {
        this.retries = retries;
    }

    @PageElement(InputText)
    @NotNull
    public String getTitle() {
        return title;
    }

    @ModuleOption(label = "邮件标题",value = "email title.")
    public void setTitle(String title) {
        this.title = title;
    }

    @PageElement(TextArea)
    @NotNull
    public String getEmailContent() {
        return emailContent;
    }
    
    @ModuleOption(label = "邮件内容",value = "email content.")
    public void setEmailContent(String emailContent) {
        this.emailContent = emailContent;
    }
}
