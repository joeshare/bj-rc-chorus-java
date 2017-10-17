package cn.rongcapital.chorus.modules.hive2vertica;

import org.springframework.xd.module.options.spi.*;
import org.springframework.xd.module.options.spi.Source;
import org.springframework.xd.module.options.spi.page.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.xd.module.options.spi.page.PageElementType.*;
import static org.springframework.xd.module.options.spi.page.PageElementType.InputText;

/**
 * Created by Athletics on 2017/8/22.
 */
@Mixin({})
public class Hive2VerticaMetadata {

    private Long projectId;

    private String tableId;

    private String dwConnectUrl;

    private String dwDbName;

    private String dwTableName;

    private String dwUserName;

    private String dwLocation;

    private String importStrategy;

    private String verticaConnectUrl;

    private String verticaUserName;

    private String verticaPassword;

    private String verticaTable;

    private String where;

    private String columnNameMapStr;

    private Integer retryCount;

    private static transient List<PageOption> importStrategyList = new ArrayList<PageOption>() {{
        add(new PageOption("全量导出", ImportStrategy.ALL));
        add(new PageOption("sql导出", ImportStrategy.SQL));
    }};

    @PageElement(InputText)
    public Long getProjectId() {
        return projectId;
    }

    @ModuleOption(value = "project id ", hidden = true, achieveMethod = AchieveMethod.CONTEXT)
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    @PageElement(InputText)
    @NotNull
    public String getDwConnectUrl() {
        return dwConnectUrl;
    }

    @ModuleOption(value = "dw connect url", hidden = true, achieveMethod = AchieveMethod.CONTEXT)
    public void setDwConnectUrl(String dwConnectUrl) {
        this.dwConnectUrl = dwConnectUrl;
    }

    @PageElement(InputText)
    @NotNull
    public String getDwDbName() {
        return dwDbName;
    }

    @ModuleOption(value = "dw db name", hidden = true, achieveMethod = AchieveMethod.CONTEXT)
    public void setDwDbName(String dwDbName) {
        this.dwDbName = dwDbName;
    }

    @PageElement(DropDown)
    @NotNull
    public String getDwTableName() {
        return dwTableName;
    }

    @ModuleOption(value = "dw table name", from = {"projectId"}, order = 7)
    @Source(url = "/internal_datasource/table", param = {
            @SourceParam(key = "projectId", value = "projectId")
    }, metaData = {
            @SourceMetaData(key = "value", value = "name"),
            @SourceMetaData(key = "displayValue", value = "name"),
    })
    @Action(assign = true)
    @AssignAction(options = {
            @AssignActionOption(key = "tableId", value = "id")
    })
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

    @PageElement(Radio)
    @PageOptionList("importStrategyList")
    public String getImportStrategy() {
        return importStrategy;
    }

    @ModuleOption(value = "import strategy ", to = {"where"}, order = 5)
    @Action(control = true)
    @ControlAction(options = {
            @ControlActionOption(key = "ALL", display = {}, hidden = {"where"}),
            @ControlActionOption(key = "SQL", display = {"where"}, hidden = {}),
    })
    public void setImportStrategy(String importStrategy) {
        this.importStrategy = importStrategy;
    }

    @PageElement(InputText)
    @NotNull
    public String getVerticaConnectUrl() {
        return verticaConnectUrl;
    }

    @ModuleOption(value = "vertica connect url")
    public void setVerticaConnectUrl(String verticaConnectUrl) {
        this.verticaConnectUrl = verticaConnectUrl;
    }

    @PageElement(InputText)
    @NotNull
    public String getVerticaUserName() {
        return verticaUserName;
    }

    @ModuleOption(value = "vertica user name")
    public void setVerticaUserName(String verticaUserName) {
        this.verticaUserName = verticaUserName;
    }

    @PageElement(Password)
    @NotNull
    public String getVerticaPassword() {
        return verticaPassword;
    }

    @ModuleOption(value = "vertica password")
    public void setVerticaPassword(String verticaPassword) {
        this.verticaPassword = verticaPassword;
    }

    @PageElement(InputText)
    @NotNull
    public String getVerticaTable() {
        return verticaTable;
    }

    @ModuleOption(value = "vertica table")
    public void setVerticaTable(String verticaTable) {
        this.verticaTable = verticaTable;
    }

    @PageElement(InputText)
    public String getTableId() {
        return tableId;
    }

    @ModuleOption(value = "internal table id", hidden = true, from = {"dwTableName"})
    @Action(broadcast = {"columnNameMapStr"})
    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    @PageElement(TextArea)
    @Variable("[{\"name\":\"$whereToday\", \"desc\":\"where内可用变量, 默认执行当天日期（yyyy-MM-dd）\"}," +
            "{\"name\":\"$whereYesterday\", \"desc\":\"where内可用变量, 默认执行前一天日期（yyyy-MM-dd）\"}," +
            "{\"name\":\"$whereVar1\", \"desc\":\"where内可用变量1\"}," +
            "{\"name\":\"$whereVar2\", \"desc\":\"where内可用变量2\"}," +
            "{\"name\":\"$whereVar3\", \"desc\":\"where内可用变量3\"}," +
            "{\"name\":\"$whereVar4\", \"desc\":\"where内可用变量4\"}," +
            "{\"name\":\"$whereVar5\", \"desc\":\"where内可用变量5\"}]")
    public String getWhere() {
        return where;
    }

    @ModuleOption(value = "dw where clause", defaultValue = "", from = {"importStrategy"}, order = 6)
    public void setWhere(String where) {
        this.where = where;
    }

    @PageElement(MultiPairInputText)
    @NotNull
    @Size(min = 1)
    public String getColumnNameMapStr() {
        return columnNameMapStr;
    }

    @ModuleOption(value = "dw to vertica column name mapping", from = {"dwTableName"}, order = 9)
    @Sources(
            {
                    @Source(url = "/internal_datasource/field", param = {
                            @SourceParam(key = "tableId", value = "tableId")
                    }, metaData = {
                            @SourceMetaData(key = "value", value = "name"),
                            @SourceMetaData(key = "displayValue", value = "name"),
                    })
            }
    )
    public void setColumnNameMapStr(String columnNameMapStr) {
        this.columnNameMapStr = columnNameMapStr;
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

enum ImportStrategy {
    ALL, SQL
}
