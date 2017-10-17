package cn.rongcapital.chorus.module.hdfsfilecopy;

import org.springframework.xd.module.options.spi.Mixin;
import org.springframework.xd.module.options.spi.ModuleOption;
import org.springframework.xd.module.options.spi.page.PageElement;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static org.springframework.xd.module.options.spi.page.PageElementType.*;

/**
 * Created by yangdawei on 22/05/2017.
 */
@Mixin({})
public class ImportDataMetadata {
    
	private String hdfsFileSourcePath;
	private String hdfsFileTargetPath;
	
	@PageElement(InputText)
	@NotNull
	public String getHdfsFileSourcePath(){
		return this.hdfsFileSourcePath;
	}
	
	@ModuleOption(value = "Source Path")
	public void setHDFSFileSourcePath(String hdfsFileSourcePath ){
		this.hdfsFileSourcePath = hdfsFileSourcePath;
	}
	
	@PageElement(InputText)
	@NotNull
	public String getHdfsFileTargetPath(){
		return this.hdfsFileTargetPath;
	}
	
	@ModuleOption(value = "Target Path")
	public void setHdfsFileTargetPath(String hdfsFileTargetpath ){
		this.hdfsFileTargetPath = hdfsFileTargetpath;
	}
	  
}
