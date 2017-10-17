package cn.rongcapital.chorus.modules.json2hive;

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

@Mixin({})
public class Json2HiveMetadata {

    private String filePath;
    private String dwConnectUrl;
    private String dwDbName;
    private Long projectId;
    private String dwTableName;
    private String dwUserName;
    private String dwLocation;
    private String dataCoverStrategy;
    private String tableId;
    private String columnNameMapStr;
    private String partitionMapStr;
    private Integer retryCount;

    private static transient List<PageOption> dataCoverStrategyList = new ArrayList<PageOption>() {{
        add(new PageOption("同分区追加", DataCoverStrategy.PARTITION_ADD));
        add(new PageOption("同分区覆盖", DataCoverStrategy.PARTITION_COVER));
    }};

    @PageElement(InputText)
    @NotNull
    public String getFilePath() {
        return filePath;
    }

    @ModuleOption(label = "文件路径", value = "file path", order = 1)
    @Action(broadcast = {"columnNameMapStr"})
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @PageElement(InputText)
    @NotNull
    public String getDwConnectUrl() {
        return dwConnectUrl;
    }

    @ModuleOption(label = "数据库地址", value = "dw connect url", hidden = true, achieveMethod = AchieveMethod.CONTEXT)
    public void setDwConnectUrl(String dwConnectUrl) {
        this.dwConnectUrl = dwConnectUrl;
    }

    @PageElement(InputText)
    @NotNull
    public String getDwDbName() {
        return dwDbName;
    }

    @ModuleOption(label = "数据库名", value = "dw db name", hidden = true)
    public void setDwDbName(String dwDbName) {
        this.dwDbName = dwDbName;
    }

    @PageElement(InputText)
    public Long getProjectId() {
        return projectId;
    }

    @ModuleOption(value = "project id ", hidden = true, achieveMethod = AchieveMethod.CONTEXT)
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    @PageElement(DropDown)
    @NotNull
    public String getDwTableName() {
        return dwTableName;
    }

    @ModuleOption(label = "数据库表名", value = "dw table name", from = {"projectId"}, order = 5)
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

    @ModuleOption(label = "数据库用户名", value = "dw user name", hidden = true, defaultValue = "hive")
    @Action(broadcast = {"columnNameMapStr"})
    public void setDwUserName(String dwUserName) {
        this.dwUserName = dwUserName;
    }

    @PageElement(InputText)
    @NotNull
    public String getDwLocation() {
        return dwLocation;
    }

    @ModuleOption(label = "数据库文件路径", value = "dw file location", hidden = true)
    public void setDwLocation(String dwLocation) {
        this.dwLocation = dwLocation;
    }

    @PageElement(Radio)
    @PageOptionList("dataCoverStrategyList")
    public String getDataCoverStrategy() {
        return dataCoverStrategy;
    }

    @ModuleOption(label = "录入方式", value = "data cover strategy", order = 4)
    public void setDataCoverStrategy(String dataCoverStrategy) {
        this.dataCoverStrategy = dataCoverStrategy;
    }

    @PageElement(InputText)
    public String getTableId() {
        return tableId;
    }

    @ModuleOption(value = "internal table id", hidden = true, from = {"dwTableName"})
    @Action(broadcast = {"partitionMapStr","columnNameMapStr"})
    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    @PageElement(MultiPairTable)
    @NotNull
    @Size(min = 1)
    public String getColumnNameMapStr() {
        return columnNameMapStr;
    }

    @ModuleOption(label = "列属性映射", value = "json to dw column name mapping", from = {"filePath", "dwTableName"}, order = 5)
    @Sources(
            {
                    @Source(url = "/internal_datasource/field", param = {
                            @SourceParam(key = "tableId", value = "tableId")
                    }, metaData = {
                            @SourceMetaData(key = "value", value = "name"),
                            @SourceMetaData(key = "displayValue", value = "name"),
                    }),
                    @Source(url = "/external_datasource/jsonfield", param = {
                            @SourceParam(key = "filePath", value = "filePath"),
                            @SourceParam(key = "dwUserName", value = "dwUserName"),
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

    @ModuleOption(label = "分区属性", value = "json to dw partition column mapping: " +
            " $executionDate sets the value to the execution date, in yyyy-MM-dd pattern; " +
            " #*** sets the value to the value in that column, *** is the column name in rdb; ", order = 6)
    @Sources(
            {
                    @Source(url = "/internal_datasource/partition", param = {
                            @SourceParam(key = "tableId", value = "tableId")
                    }, metaData = {
                            @SourceMetaData(key = "value", value = "name"),
                            @SourceMetaData(key = "displayValue", value = "name"),
                    })
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

    @ModuleOption(label = "重试次数", value = "retry count", defaultValue = "0")
    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

}

enum DataCoverStrategy {
    PARTITION_ADD, PARTITION_COVER
}