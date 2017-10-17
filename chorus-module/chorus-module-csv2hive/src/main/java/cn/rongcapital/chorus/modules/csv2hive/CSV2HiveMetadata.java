package cn.rongcapital.chorus.modules.csv2hive;

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
 * Created by maboxiao on 08/08/2017.
 */
@Mixin({})
public class CSV2HiveMetadata {

    private String csvFilePath;
    private String hasTitle;
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

    private static transient List<PageOption> csvFileHasTitleList = new ArrayList<PageOption>() {{
        add(new PageOption("没有表头", CSVFileHasTitle.NOT_HAVE));
        add(new PageOption("有表头", CSVFileHasTitle.HAVE));
    }};

    private static transient List<PageOption> dataCoverStrategyList = new ArrayList<PageOption>() {{
        add(new PageOption("同分区追加", DataCoverStrategy.PARTITION_ADD));
        add(new PageOption("同分区覆盖", DataCoverStrategy.PARTITION_COVER));
    }};

    @PageElement(InputText)
    @NotNull
    @Variable("[{\"name\":\"$systemDate\", \"desc\":\"默认执行当天日期（yyyy-MM-dd）\"}]")
    public String getCsvFilePath() {
        return csvFilePath;
    }

    @ModuleOption(value = "csv file path", order = 1)
    @Action(broadcast = {"columnNameMapStr"})
    public void setCsvFilePath(String csvFilePath) {
        this.csvFilePath = csvFilePath;
    }

    @PageElement(Radio)
    @PageOptionList("csvFileHasTitleList")
    @NotNull
    public String getHasTitle() {
        return hasTitle;
    }

    @ModuleOption(value = "csv file has title.", defaultValue = "NOT_HAVE", order = 2)
    @Action(broadcast = {"columnNameMapStr"})
    public void setHasTitle(String hasTitle) {
        this.hasTitle = hasTitle;
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

    @ModuleOption(value = "dw db name", hidden = true)
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

    @ModuleOption(value = "dw table name", from = {"projectId"}, order = 5)
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
    @Action(broadcast = {"columnNameMapStr"})
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
    @PageOptionList("dataCoverStrategyList")
    public String getDataCoverStrategy() {
        return dataCoverStrategy;
    }

    @ModuleOption(value = "data cover strategy", order = 4)
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

    @ModuleOption(value = "csv to dw column name mapping", from = {"csvFilePath", "dwTableName"}, order = 5)
    @Sources(
            {
                    @Source(url = "/internal_datasource/field", param = {
                            @SourceParam(key = "tableId", value = "tableId")
                    }, metaData = {
                            @SourceMetaData(key = "value", value = "name"),
                            @SourceMetaData(key = "displayValue", value = "name"),
                    }),
                    @Source(url = "/external_datasource/csvfield", param = {
                            @SourceParam(key = "csvFilePath", value = "csvFilePath"),
                            @SourceParam(key = "dwUserName", value = "dwUserName"),
                            @SourceParam(key = "hasTitle", value = "hasTitle"),
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

    @ModuleOption(value = "csv to dw partition column mapping: " +
            " $executionDate sets the value to the execution date, in yyyy-MM-dd pattern; " +
            " #*** sets the value to the value in that column, *** is the column name in rdb; ", order = 6)
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

    @ModuleOption(value = "retry count", defaultValue = "0")
    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

}

enum CSVFileHasTitle {
    NOT_HAVE, HAVE
}

enum DataCoverStrategy {
    PARTITION_ADD, PARTITION_COVER
}