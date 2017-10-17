package cn.rongcapital.chorus.modules.transfer.hdfs.sftp;

import org.springframework.xd.module.options.spi.Mixin;
import org.springframework.xd.module.options.spi.ModuleOption;
import org.springframework.xd.module.options.spi.page.PageElement;
import org.springframework.xd.module.options.spi.page.PageOption;
import org.springframework.xd.module.options.spi.page.PageOptionList;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.xd.module.options.spi.page.PageElementType.*;
import static org.springframework.xd.module.options.spi.page.PageElementType.InputText;

/**
 * Created by hhlfl on 2017-8-14.
 */
@Mixin({})
public class Hdfs2SftpMetadata {
    static final Byte AUTH_TYPE_PASSWORD = 0x1;
    static final Byte AUTH_TYPE_KEY = 0x2;
    private String sftpHost;
    private Integer sftpHostPort;
    private String sftpUserName;
    private Byte sftpAuthType;
    private String sftpPassword;
    private String sftpPrivateKey;
    private String sftpFilePath;
    private String hdfsFileFolderPath;
    private Integer retryCount;

    @PageElement(InputText)
    @NotNull
    public String getSftpHost() {
        return sftpHost;
    }

    @ModuleOption(value = "sftp host")
    public void setSftpHost(String sftpHost) {
        this.sftpHost = sftpHost;
    }

    @PageElement(InputText)
    @NotNull
    @Min(22)
    @Max(65535)
    public Integer getSftpHostPort() {
        return sftpHostPort;
    }

    @ModuleOption(value = "sftp host port", defaultValue = "22")
    public void setSftpHostPort(Integer sftpHostPort) {
        this.sftpHostPort = sftpHostPort;
    }

    @PageElement(InputText)
    @NotNull
    public String getSftpUserName() {
        return sftpUserName;
    }

    @ModuleOption("sftp username")
    public void setSftpUserName(String sftpUserName) {
        this.sftpUserName = sftpUserName;
    }

    @PageElement(Password)
    public String getSftpPassword() {
        return sftpPassword;
    }

    @ModuleOption("sftp password")
    public void setSftpPassword(String sftpPassword) {
        this.sftpPassword = sftpPassword;
    }

    private static transient List<PageOption> sftpAuthTypeList = new ArrayList<PageOption>() {{
        add(new PageOption("password", AUTH_TYPE_PASSWORD));
        add(new PageOption("key", AUTH_TYPE_KEY));
    }};

    @PageElement(Radio)
    @PageOptionList("sftpAuthTypeList")
    @NotNull
    @Min(1)
    @Max(2)
    public Byte getSftpAuthType() {
        return sftpAuthType;
    }

    @ModuleOption("sftp auth type, password or key.")
    public void setSftpAuthType(Byte sftpAuthType) {
        this.sftpAuthType = sftpAuthType;
    }

    @PageElement(TextArea)
    public String getSftpPrivateKey() {
        return sftpPrivateKey;
    }

    @ModuleOption("sftp private key")
    public void setSftpPrivateKey(String sftpPrivateKey) {
        this.sftpPrivateKey = sftpPrivateKey;
    }

    @PageElement(InputText)
    @NotNull
    @Pattern(regexp = "/.*")
    public String getHdfsFileFolderPath() {
        return hdfsFileFolderPath;
    }

    @ModuleOption("hdfs file folder path.")
    public void setHdfsFileFolderPath(String hdfsFileFolderPath) {
        this.hdfsFileFolderPath = hdfsFileFolderPath;
    }

    @PageElement(InputText)
    @NotNull
    @Pattern(regexp = "/.+")
    public String getSftpFilePath() {
        return sftpFilePath;
    }

    @ModuleOption("sftp file folder path list.")
    public void setSftpFilePath(String sftpFilePath) {
        this.sftpFilePath = sftpFilePath;
    }

    @PageElement(InputText)
    @Min(0)
    @Max(3)
    public Integer getRetryCount() {
        return retryCount;
    }

    @ModuleOption(value = "retry count", defaultValue = "0")
    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }
}
