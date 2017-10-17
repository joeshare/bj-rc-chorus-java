package cn.rongcapital.chorus.modules.file_sample;

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

/**
 * Created by abiton on 15/06/2017.
 */
@Mixin({})
public class FileSampleMetadata {

    private String path;

    private String sampleType;

    private Integer sampleRate;

    private Integer sampleCount;

    private String outputPath;

    private static transient List<PageOption> sampleTypeList = new ArrayList<PageOption>() {{
        add(new PageOption("百分比", SampleType.RATE));
        add(new PageOption("行数", SampleType.COUNT));
    }};
    @PageElement(InputText)
    @NotNull
    public String getPath(){
       return path;
    }

    @ModuleOption("源文件路径")
    public void setPath(String path){
        this.path = path;
    }

    @PageElement(InputText)
    @NotNull
    public String getOutputPath(){
        return outputPath;
    }

    @ModuleOption("输出文件路径")
    public void setOutputPath(String path){
        this.outputPath = path;
    }

    @PageElement(Radio)
    @PageOptionList("sampleTypeList")
    @NotNull
    public String getSampleType(){
        return this.sampleType;
    }

    @ModuleOption("抽样类型:百分比/行数")
    public void setSampleType(String sampleType){
        this.sampleType = sampleType;
    }

    @PageElement(InputText)
    public Integer getSampleRate(){
        return sampleRate;
    }

    @ModuleOption(value = "百分比",defaultValue = "10")
    public void setSampleRate(Integer sampleRate){
        this.sampleRate = sampleRate;
    }

    @PageElement(InputText)
    public Integer getSampleCount(){
        return sampleCount;
    }

    @ModuleOption(value = "行数",defaultValue = "10000")
    public void setSampleCount(Integer sampleCount){
        this.sampleCount = sampleCount;
    }
}

enum SampleType{
    RATE,
    COUNT
}
