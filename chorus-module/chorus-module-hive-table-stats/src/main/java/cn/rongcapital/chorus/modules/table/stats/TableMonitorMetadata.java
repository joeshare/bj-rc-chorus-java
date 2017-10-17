package cn.rongcapital.chorus.modules.table.stats;

import org.springframework.xd.module.options.spi.Mixin;
import org.springframework.xd.module.options.spi.ModuleOption;
import org.springframework.xd.module.options.spi.page.PageElement;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import static org.springframework.xd.module.options.spi.page.PageElementType.InputText;
import static org.springframework.xd.module.options.spi.page.PageElementType.Password;

@Mixin({})
public class TableMonitorMetadata {
    // chorus DB URL
    private String chorusURL;
    // 用户名称
    private String chorusUser;
    // 用户密码
    private String chorusPassword;
    // hive URL
    private String hiveURL;
    // hive db 用户名称
    private String hiveUser;
    // hive 密码
    private String hivePassword;

    private String atlasURL;

    private String atlasUser;

    private String atlasPassword;

    private int retries;

    @PageElement(InputText)
    @NotNull
    public String getChorusURL() {
        return chorusURL;
    }

    @ModuleOption(value = "chorus mysql db url.", defaultValue = "\"jdbc:mysql://{ip}:{port}/{db}?useUnicode=true&characterEncoding=utf-8&useSSL=false\"")
    public void setChorusURL(String chorusURL) {
        this.chorusURL = chorusURL;
    }

    @PageElement(InputText)
    @NotNull
    public String getChorusUser() {
        return chorusUser;
    }

    @ModuleOption(value = "chours db user name")
    public void setChorusUser(String chorusUser) {
        this.chorusUser = chorusUser;
    }

    @PageElement(Password)
    @NotNull
    public String getChorusPassword() {
        return chorusPassword;
    }

    @ModuleOption(value = "chorus db password")
    public void setChorusPassword(String chorusPassword) {
        this.chorusPassword = chorusPassword;
    }

    @PageElement(InputText)
    @NotNull
    public String getHiveURL() {
        return hiveURL;
    }

    @ModuleOption(value = "xd db url", defaultValue = "\"jdbc:mysql://{ip}:{port}/{db}?useUnicode=true&characterEncoding=utf-8&useSSL=false\"")
    public void setHiveURL(String hiveURL) {
        this.hiveURL = hiveURL;
    }

    @PageElement(InputText)
    @NotNull
    public String getHiveUser() {
        return hiveUser;
    }

    @ModuleOption(value = "xd db user name")
    public void setHiveUser(String hiveUser) {
        this.hiveUser = hiveUser;
    }

    @PageElement(Password)
    @NotNull
    public String getHivePassword() {
        return hivePassword;
    }

    @ModuleOption(value = "xd db password")
    public void setHivePassword(String hivePassword) {
        this.hivePassword = hivePassword;
    }

    @PageElement(InputText)
    @Max(10)
    @Min(0)
    public int getRetries() {
        return retries;
    }

    @ModuleOption(value = "number of retries.")
    public void setRetries(int retries) {
        this.retries = retries;
    }

    @PageElement(InputText)
    @NotNull
    public String getAtlasURL() {
        return atlasURL;
    }

    @ModuleOption(value = "atlas url.")
    public void setAtlasURL(String atlasURL) {
        this.atlasURL = atlasURL;
    }

    @PageElement(InputText)
    @NotNull
    public String getAtlasUser() {
        return atlasUser;
    }

    @ModuleOption(value = "atlas user.")
    public void setAtlasUser(String atlasUser) {
        this.atlasUser = atlasUser;
    }

    @PageElement(Password)
    @NotNull
    public String getAtlasPassword() {
        return atlasPassword;
    }

    @ModuleOption(value = "atlas password.")
    public void setAtlasPassword(String atlasPassword) {
        this.atlasPassword = atlasPassword;
    }

}
