package cn.rongcapital.chorus.modules.retrydemo;

import static org.springframework.xd.module.options.spi.page.PageElementType.InputText;
import static org.springframework.xd.module.options.spi.page.PageElementType.MultiPairInputText;
import static org.springframework.xd.module.options.spi.page.PageElementType.Password;
import static org.springframework.xd.module.options.spi.page.PageElementType.TextArea;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.xd.module.options.spi.Mixin;
import org.springframework.xd.module.options.spi.ModuleOption;
import org.springframework.xd.module.options.spi.page.PageElement;

@Mixin({})
public class RetryDemoMetadata {

    /******** copy importData module begin ********/
    private String dwConnectUrl;
    private String dwDbName;
    private String dwTableName;
    private String dwUserName;
    private String dwLocation;
    private String rdbConnectUrl;
    private String rdbUserName;
    private String rdbPassword;
    private String rdbTable;
    private String where;
    private String columnNameMapStr;
    private String partitionMapStr;

    @PageElement(InputText)
    @NotNull
    public String getDwConnectUrl() {
        return dwConnectUrl;
    }

    @ModuleOption(value = "dw connect url", hidden = true)
    public void setDwConnectUrl(String dwConnectUrl) {
        this.dwConnectUrl = dwConnectUrl;
    }

    @PageElement(InputText)
    @NotNull
    public String getDwDbName() {
        return dwDbName;
    }

    @ModuleOption(value = "dw db name", hidden = true)
    public void setDwDbName(String dwDbName) {
        this.dwDbName = dwDbName;
    }

    @PageElement(InputText)
    @NotNull
    public String getDwTableName() {
        return dwTableName;
    }

    @ModuleOption(value = "dw table name")
    public void setDwTableName(String dwTableName) {
        this.dwTableName = dwTableName;
    }

    @PageElement(InputText)
    @NotNull
    public String getDwUserName() {
        return dwUserName;
    }

    @ModuleOption(value = "dw user name", hidden = true, defaultValue = "hive")
    public void setDwUserName(String dwUserName) {
        this.dwUserName = dwUserName;
    }

    @PageElement(InputText)
    @NotNull
    public String getDwLocation() {
        return dwLocation;
    }

    @ModuleOption(value = "dw file location", hidden = true)
    public void setDwLocation(String dwLocation) {
        this.dwLocation = dwLocation;
    }

    @PageElement(InputText)
    @NotNull
    public String getRdbConnectUrl() {
        return rdbConnectUrl;
    }

    @ModuleOption(value = "rdb connect url")
    public void setRdbConnectUrl(String rdbConnectUrl) {
        this.rdbConnectUrl = rdbConnectUrl;
    }

    @PageElement(InputText)
    @NotNull
    public String getRdbUserName() {
        return rdbUserName;
    }

    @ModuleOption(value = "rdb user name")
    public void setRdbUserName(String rdbUserName) {
        this.rdbUserName = rdbUserName;
    }

    @PageElement(Password)
    @NotNull
    public String getRdbPassword() {
        return rdbPassword;
    }

    @ModuleOption(value = "rdb password")
    public void setRdbPassword(String rdbPassword) {
        this.rdbPassword = rdbPassword;
    }

    @PageElement(InputText)
    @NotNull
    public String getRdbTable() {
        return rdbTable;
    }

    @ModuleOption(value = "rdb table")
    public void setRdbTable(String rdbTable) {
        this.rdbTable = rdbTable;
    }

    @PageElement(TextArea)
    public String getWhere() {
        return where;
    }

    @ModuleOption(value = "rdb where clause", defaultValue = "")
    public void setWhere(String where) {
        this.where = where;
    }

    @PageElement(MultiPairInputText)
    @NotNull
    @Size(min = 1)
    public String getColumnNameMapStr() {
        return columnNameMapStr;
    }

    @ModuleOption(value = "rdb to dw column name mapping")
    public void setColumnNameMapStr(String columnNameMapStr) {
        this.columnNameMapStr = columnNameMapStr;
    }

    @PageElement(MultiPairInputText)
    @NotNull
    @Size(min = 1)
    public String getPartitionMapStr() {
        return partitionMapStr;
    }

    @ModuleOption(value = "rdb to dw partition column mapping: "
            + " $executionDate sets the value to the execution date, in yyyy-MM-dd pattern; "
            + " #*** sets the value to the value in that column, *** is the column name in rdb; ")
    public void setPartitionMapStr(String partitionMapStr) {
        this.partitionMapStr = partitionMapStr;
    }

    /******** copy importData module end ********/

    private Integer retryCount;

    @PageElement(InputText)
    @Min(0)
    @Max(10)
    public Integer getRetryCount() {
        return retryCount;
    }

    @ModuleOption(value = "retry count", defaultValue = "0")
    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }
}
