package cn.rongcapital.chorus.modules.python;

import org.springframework.xd.module.options.spi.Mixin;
import org.springframework.xd.module.options.spi.ModuleOption;
import org.springframework.xd.module.options.spi.page.PageElement;

import javax.validation.constraints.NotNull;

import static org.springframework.xd.module.options.spi.page.PageElementType.InputText;

/**
 * Created by abiton on 08/06/2017.
 */
@Mixin({})
public class PythonMetadata {
    private String scriptPath;


    @PageElement(InputText)
    @NotNull
    public String getScriptPath() {
        return scriptPath;
    }

    @ModuleOption("python script path ")
    public void setScriptPath(String scriptPath) {
        this.scriptPath = scriptPath;
    }
}
