package cn.rongcapital.chorus.common.xd.model;

import org.springframework.xd.rest.domain.JobDefinitionResource;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author li.hzh
 * @date 2016-11-16 14:51
 */
@XmlRootElement
public class XDJobDefinitionResource extends JobDefinitionResource {
    
    private JobDefinitionResource jobDefinitionResource;
    
    public XDJobDefinitionResource(String name, String definition) {
        super(name, definition);
    }
    
    public XDJobDefinitionResource(JobDefinitionResource jobDefinitionResource) {
        super(jobDefinitionResource.getName(), jobDefinitionResource.getDefinition());
        this.jobDefinitionResource = jobDefinitionResource;
        setStatus(jobDefinitionResource.getStatus());
    }
    
}
