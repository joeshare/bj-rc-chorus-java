package cn.rongcapital.chorus.modules.hdfs.snapshot;

import static org.springframework.xd.module.options.spi.page.PageElementType.InputText;

import javax.validation.constraints.NotNull;

import org.springframework.xd.module.options.spi.Mixin;
import org.springframework.xd.module.options.spi.ModuleOption;
import org.springframework.xd.module.options.spi.page.PageElement;

@Mixin({})
public class HdfsSnapshotMetaData {
    private String dir;
    private String hdfsUrl;
    private Integer remainDays;

    @PageElement(InputText)
    @NotNull
    public String getDir() {
        return dir;
    }

    @ModuleOption(value = " create snapshot directory", defaultValue ="/chorus/project")
    public void setDir(String dir) {
        this.dir = dir;
    }

    @PageElement(InputText)
    @NotNull
    public String getHdfsUrl() {
        return hdfsUrl;
    }

    @ModuleOption(value = " hdfs url")
    public void setHdfsUrl(String hdfsUrl) {
        this.hdfsUrl = hdfsUrl;
    }

    @PageElement(InputText)
    @NotNull
    public Integer getRemainDays() {
        return remainDays;
    }
    @ModuleOption(value = "expirationDate time of snapshot", defaultValue ="7")
    public void setRemainDays(Integer remainDays) {
        this.remainDays = remainDays;
    }
}
