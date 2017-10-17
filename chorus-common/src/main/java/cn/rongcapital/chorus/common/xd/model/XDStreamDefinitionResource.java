package cn.rongcapital.chorus.common.xd.model;

import org.springframework.xd.rest.domain.StreamDefinitionResource;

/**
 * XD Stream 模型
 *
 * @author li.hzh
 * @date 2016-11-24 18:07
 */
public class XDStreamDefinitionResource extends StreamDefinitionResource {
    
    private StreamDefinitionResource streamDefinitionResource;
    
    public XDStreamDefinitionResource(String name, String definition) {
        super(name, definition);
    }
    
    public XDStreamDefinitionResource(StreamDefinitionResource streamDefinitionResource) {
        super(streamDefinitionResource.getName(), streamDefinitionResource.getDefinition());
        this.streamDefinitionResource = streamDefinitionResource;
        setStatus(streamDefinitionResource.getStatus());
    }
}
