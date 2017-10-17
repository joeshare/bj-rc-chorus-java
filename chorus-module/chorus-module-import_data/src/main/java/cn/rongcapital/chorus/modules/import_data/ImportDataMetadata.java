package cn.rongcapital.chorus.modules.import_data;

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

/**
 * Created by alan on 24/04/2017.
 */
@Mixin({})
public class ImportDataMetadata {

    private Long projectId;
    private String tableId;
    private String externalRDB;
    private String dwConnectUrl;
    private String dwDbName;
    private String dwTableName;
    private String dwUserName;
    private String dwLocation;
    private String rdbConnectUrl;
    private String rdbUserName;
    private String rdbPassword;
    private String rdbTable;
    private String rdbName;
    private String where;
    private String columnNameMapStr;
    private String partitionMapStr;
    private String importStrategy;
    private String dataCoverStrategy;
    private String dataTableType;
    private String rdbTableDynamic;
    private Integer retryCount;

    private static transient List<PageOption> importStrategyList = new ArrayList<PageOption>() {{
        add(new PageOption("全量导入", ImportStrategy.ALL));
        add(new PageOption("sql导入", ImportStrategy.SQL));
    }};
    private static transient List<PageOption> dataCoverStrategyList = new ArrayList<PageOption>() {{
        add(new PageOption("同分区追加", DataCoverStrategy.PARTITION_ADD));
        add(new PageOption("同分区覆盖", DataCoverStrategy.PARTITION_COVER));
    }};
    private static transient List<PageOption> dataTableTypeList = new ArrayList<PageOption>() {{
        add(new PageOption("静态", DataTableType.STATIC));
        add(new PageOption("动态", DataTableType.DYNAMIC));
    }};


    @PageElement(InputText)
    public Long getProjectId() {
        return projectId;
    }

    @ModuleOption(value = "project id ", hidden = true, achieveMethod = AchieveMethod.CONTEXT)
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    @PageElement(Radio)
    @PageOptionList("dataCoverStrategyList")
    public String getDataCoverStrategy() {
        return dataCoverStrategy;
    }

    @ModuleOption(label = "数据覆盖策略",value = "data cover strategy", order = 8, group = "2", groupDesc = "目标表")
    public void setDataCoverStrategy(String dataCoverStrategy) {
        this.dataCoverStrategy = dataCoverStrategy;
    }

    @PageElement(DropDown)
    public String getExternalRDB() {
        return externalRDB;
    }

    @ModuleOption(label = "外部数据源",value = "external RDB", to = {"rdbConnectUrl", "rdbUserName", "rdbPassword", "rdbTable"}, order = 1, group = "1", groupDesc = "源数据表")
    @Source(url = "/external_datasource/rdb", param = {@SourceParam(key = "projectId", value = "projectId")},
            metaData = {
                    @SourceMetaData(key = "value", value = "name"),
                    @SourceMetaData(key = "displayValue", value = "name"),
            }
    )

    @Action(assign = true, broadcast = {"rdbTable"})
    @AssignAction(options = {
            @AssignActionOption(key = "rdbConnectUrl", value = "connUrl"),
            @AssignActionOption(key = "rdbUserName", value = "connUser"),
            @AssignActionOption(key = "rdbPassword", value = "connPass"),
            @AssignActionOption(key = "rdbName", value = "name"),
    })
    public void setExternalRDB(String externalRDB) {
        this.externalRDB = externalRDB;
    }

    @PageElement(Radio)
    @PageOptionList("dataTableTypeList")
    public String getDataTableType() {
        return dataTableType;
    }

    @ModuleOption(label = "源表类型",value = "data table type ", to = {"rdbTableDynamic"}, order = 2, group = "1", groupDesc = "源数据表")
    @Action(control = true)
    @ControlAction(options = {
            @ControlActionOption(key = "STATIC", display = {}, hidden = {"rdbTableDynamic"}),
            @ControlActionOption(key = "DYNAMIC", display = {"rdbTableDynamic"}, hidden = {}),
    })
    public void setDataTableType(String dataTableType) {
        this.dataTableType = dataTableType;
    }

    @PageElement(InputText)
    @Variable("[{\"name\":\"$systemDate\", \"desc\":\"默认执行当天日期（yyyyMMdd）\"}]")
    public String getRdbTableDynamic() {
        return rdbTableDynamic;
    }

    @ModuleOption(value = "rdb table dynamic", defaultValue = "", from = {"dataTableType"}, order = 4)
    public void setRdbTableDynamic(String rdbTableDynamic) {
        this.rdbTableDynamic = rdbTableDynamic;
    }

    @PageElement(Radio)
    @PageOptionList("importStrategyList")
    public String getImportStrategy() {
        return importStrategy;
    }

    @ModuleOption(label = "导入策略",value = "import strategy ", to = {"where"}, order = 5, group = "1", groupDesc = "源数据表")
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

    @ModuleOption(label = "目标表",value = "dw table name", from = {"projectId"}, order = 7, group = "2", groupDesc = "目标表")
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

    @PageElement(InputText)
    @NotNull
    public String getRdbConnectUrl() {
        return rdbConnectUrl;
    }

    @ModuleOption(value = "rdb connect url",hidden = true, from = {"externalRDB"})
    @Action(broadcast = {"rdbTable"})
    public void setRdbConnectUrl(String rdbConnectUrl) {
        this.rdbConnectUrl = rdbConnectUrl;
    }

    @PageElement(InputText)
    @NotNull
    public String getRdbUserName() {
        return rdbUserName;
    }

    @ModuleOption(value = "rdb user name",hidden = true, from = {"externalRDB"})
    @Action(broadcast = {"rdbTable"})
    public void setRdbUserName(String rdbUserName) {
        this.rdbUserName = rdbUserName;
    }

    @PageElement(Password)
    @NotNull
    public String getRdbPassword() {
        return rdbPassword;
    }

    @ModuleOption(value = "rdb password",hidden = true, from = {"externalRDB"})
    @Action(broadcast = {"rdbTable"})
    public void setRdbPassword(String rdbPassword) {
        this.rdbPassword = rdbPassword;
    }

    @PageElement(DropDown)
    public String getRdbTable() {
        return rdbTable;
    }

    @ModuleOption(label = "源表",value = "rdb table", from = {"rdbConnectUrl", "rdbUserName", "rdbPassword"}, order = 3, group = "1", groupDesc = "源数据表")
    @Source(url = "/external_datasource/table", param = {
            @SourceParam(key = "url", value = "rdbConnectUrl"),
            @SourceParam(key = "userName", value = "rdbUserName"),
            @SourceParam(key = "password", value = "rdbPassword"),
    }, metaData = {
            @SourceMetaData(key = "value", value = "name"),
            @SourceMetaData(key = "displayValue", value = "name"),
    })
    @Action(broadcast = {"columnNameMapStr"})
    public void setRdbTable(String rdbTable) {
        this.rdbTable = rdbTable;
    }

    @PageElement(InputText)
    public String getRdbName() {
        return rdbName;
    }

    @ModuleOption(value = "rdbName", hidden = true,from = {"externalRDB"})
    public void setRdbName(String rdbName) {
        this.rdbName = rdbName;
    }
    @PageElement(InputText)
    public String getTableId() {
        return tableId;
    }

    @ModuleOption(value = "internal table id", hidden = true, from = {"rdbTable"})
    @Action(broadcast = {"partitionMapStr","columnNameMapStr"})
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

    @ModuleOption(label = "where 条件",value = "rdb where clause", defaultValue = "", from = {"importStrategy"}, order = 6, group = "1", groupDesc = "源数据表")
    public void setWhere(String where) {
        this.where = where;
    }

    @PageElement(MultiPairTable)
    @NotNull
    @Size(min = 1)
    public String getColumnNameMapStr() {
        return columnNameMapStr;
    }

    @ModuleOption(label = "导入字段映射",value = "rdb to dw column name mapping", from = {"rdbTable", "dwTableName"}, order = 9, group = "3", groupDesc = "字段映射")
    @Sources(
            {
                    @Source(url = "/internal_datasource/field", param = {
                            @SourceParam(key = "tableId", value = "tableId")
                    }, metaData = {
                            @SourceMetaData(key = "value", value = "name"),
                            @SourceMetaData(key = "displayValue", value = "name"),
                    }),
                    @Source(url = "/external_datasource/field", param = {
                            @SourceParam(key = "url", value = "rdbConnectUrl"),
                            @SourceParam(key = "userName", value = "rdbUserName"),
                            @SourceParam(key = "password", value = "rdbPassword"),
                            @SourceParam(key = "table", value = "rdbTable"),
                    }, metaData = {
                            @SourceMetaData(key = "value", value = "name"),
                            @SourceMetaData(key = "displayValue", value = "name"),
                    })

            }
    )
    public void setColumnNameMapStr(String columnNameMapStr) {
        this.columnNameMapStr = columnNameMapStr;
    }

    @PageElement(MultiPairInputText)
    @Variable("[{\"name\":\"$executionDate\", \"desc\":\"分区[p_date]可用，默认任务执行日期（yyyy-MM-dd）\"}]")
    @NotNull
    @Size(min = 1)
    public String getPartitionMapStr() {
        return partitionMapStr;
    }

    @ModuleOption(label = "分区字段映射",value = "rdb to dw partition column mapping: " +
            " $executionDate sets the value to the execution date, in yyyy-MM-dd pattern; " +
            " #*** sets the value to the value in that column, *** is the column name in rdb; ", order = 10, group = "4", groupDesc = "分区字段映射")
    @Sources(
            {
                    @Source(url = "/internal_datasource/partition", param = {
                            @SourceParam(key = "tableId", value = "tableId")
                    }, metaData = {
                            @SourceMetaData(key = "value", value = "name"),
                            @SourceMetaData(key = "displayValue", value = "name"),
                    }),

            }
    )
    public void setPartitionMapStr(String partitionMapStr) {
        this.partitionMapStr = partitionMapStr;
    }

    @PageElement(InputText)
    @Min(0)
    @Max(3)
    public Integer getRetryCount() {
        return retryCount;
    }

    @ModuleOption(label = "重试次数",value = "retry count", defaultValue = "0", group = "5", groupDesc = "高级设置")
    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }
}

enum ImportStrategy {
    ALL, SQL
}

enum DataCoverStrategy {
    PARTITION_ADD, PARTITION_COVER
}

enum DataTableType {
    STATIC, DYNAMIC
}
