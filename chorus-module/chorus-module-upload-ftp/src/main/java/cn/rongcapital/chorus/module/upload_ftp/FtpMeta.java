package cn.rongcapital.chorus.module.upload_ftp;

import static org.springframework.xd.module.options.spi.page.PageElementType.InputText;
import static org.springframework.xd.module.options.spi.page.PageElementType.Password;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.springframework.xd.module.options.spi.Mixin;
import org.springframework.xd.module.options.spi.ModuleOption;
import org.springframework.xd.module.options.spi.page.PageElement;

/**
 * Created by alan on 19/01/2017.
 */
@Mixin({})
public class FtpMeta {
    private String ftpHost;
    private Integer ftpHostPort;
    private String ftpUserName;
    private String ftpPassword;
    private String ftpFilePath;
    private String hdfsFileFolderPath;
    private Integer retryCount;

    @PageElement(InputText)
    @NotNull
    public String getFtpHost() {
        return ftpHost;
    }

    @ModuleOption("ftp host")
    public void setFtpHost(String ftpHost) {
        this.ftpHost = ftpHost;
    }

    @PageElement(InputText)
    @NotNull
    public Integer getFtpHostPort() {
        return ftpHostPort;
    }

    @ModuleOption(value = "ftp host port", defaultValue = "21")
    public void setFtpHostPort(Integer ftpHostPort) {
        this.ftpHostPort = ftpHostPort;
    }

    @PageElement(InputText)
    @NotNull
    public String getFtpUserName() {
        return ftpUserName;
    }

    @ModuleOption("ftp username")
    public void setFtpUserName(String ftpUserName) {
        this.ftpUserName = ftpUserName;
    }

    @PageElement(Password)
    @NotNull
    public String getFtpPassword() {
        return ftpPassword;
    }

    @ModuleOption("ftp password")
    public void setFtpPassword(String ftpPassword) {
        this.ftpPassword = ftpPassword;
    }

    @PageElement(InputText)
    @NotNull
    @Pattern(regexp = "/.+")
    public String getFtpFilePath() {
        return ftpFilePath;
    }

    @ModuleOption("ftp file path, could be regex.")
    public void setFtpFilePath(String ftpFilePath) {
        this.ftpFilePath = ftpFilePath;
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
