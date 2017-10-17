package cn.rongcapital.chorus.modules.table_sample;

import org.springframework.xd.module.options.spi.Mixin;
import org.springframework.xd.module.options.spi.ModuleOption;
import org.springframework.xd.module.options.spi.page.PageElement;
import org.springframework.xd.module.options.spi.page.PageOption;
import org.springframework.xd.module.options.spi.page.PageOptionList;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.xd.module.options.spi.page.PageElementType.InputText;
import static org.springframework.xd.module.options.spi.page.PageElementType.Radio;
import static org.springframework.xd.module.options.spi.page.PageElementType.TextArea;

/**
 * Created by abiton on 19/06/2017.
 */
@Mixin({})
public class TableSampleMetadata {
    private String dwDbName;
    private String table;
    private String outputTable;
    private boolean coverBefore;
    private String dwUserName;
    private String sampleType;
    private Integer sampleRate;
    private Integer sampleCount;
    private String where;
    private String dwConnectUrl;

    @PageElement(InputText)
    @NotNull
    public String getDwConnectUrl() {
        return dwConnectUrl;
    }

    @ModuleOption(value = "DW url", hidden = true)
    public void setDwConnectUrl(String dwConnectUrl) {
        this.dwConnectUrl = dwConnectUrl;
    }

    @PageElement(Radio)
    @PageOptionList("isCoverList")
    @NotNull
    public boolean getCoverBefore() {
        return coverBefore;
    }

    @ModuleOption(value = "是否覆盖")
    public void setCoverBefore(boolean coverBefore) {
        this.coverBefore = coverBefore;
    }
    @PageElement(InputText)
    @NotNull
    public String getDwUserName() {
        return dwUserName;
    }
    @ModuleOption(value = "dw user name",hidden = true)
    public void setDwUserName(String dwUserName) {
        this.dwUserName = dwUserName;
    }

    private static transient List<PageOption> isCoverList = PageOption.BOOLEAN_PAGE_OPTION_LIST;
    private static transient List<PageOption> sampleTypeList = new ArrayList<PageOption>() {{
        add(new PageOption("百分比", SampleType.RATE));
        add(new PageOption("记录数", SampleType.COUNT));
    }};

    @PageElement(InputText)
    @NotNull
    public String getDwDbName() {
        return dwDbName;
    }

    @ModuleOption(value = "dwDbName name", hidden = true)
    public void setDwDbName(String dwDbName) {
        this.dwDbName = dwDbName;
    }

    @PageElement(InputText)
    @NotNull
    public String getTable() {
        return table;
    }

    @ModuleOption("源表表名")
    public void setTable(String table) {
        this.table = table;
    }

    @PageElement(InputText)
    @NotNull
    public String getOutputTable() {
        return outputTable;
    }

    @ModuleOption("导出表表名")
    public void setOutputTable(String outputTable) {
        this.outputTable = outputTable;
    }

    @PageElement(Radio)
    @PageOptionList("sampleTypeList")
    @NotNull
    public String getSampleType() {
        return sampleType;
    }

    @ModuleOption("抽样类型:百分比/记录数")
    public void setSampleType(String sampleType) {
        this.sampleType = sampleType;
    }

    @PageElement(InputText)
    public Integer getSampleRate() {
        return sampleRate;
    }

    @ModuleOption(value = "百分比(当抽样类型选择百分比时生效)",defaultValue = "10")
    public void setSampleRate(Integer sampleRate) {
        this.sampleRate = sampleRate;
    }

    @PageElement(InputText)
    public Integer getSampleCount() {
        return sampleCount;
    }

    @ModuleOption(value = "记录数(当抽样类型选择记录数时生效)",defaultValue = "10000")
    public void setSampleCount(Integer sampleCount) {
        this.sampleCount = sampleCount;
    }
    @PageElement(TextArea)
    public String getWhere() {
        return where;
    }

    @ModuleOption(value = "where 条件 ",defaultValue = "")
    public void setWhere(String where) {
        this.where = where;
    }
}

enum SampleType {
    RATE,
    COUNT

}
