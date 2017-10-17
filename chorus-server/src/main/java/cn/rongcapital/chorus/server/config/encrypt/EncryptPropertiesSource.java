package cn.rongcapital.chorus.server.config.encrypt;

import org.springframework.core.env.PropertiesPropertySource;

import java.util.Map;
import java.util.Properties;

/**
 * @author li.hzh
 * @date 2017-04-10 13:49
 */
public class EncryptPropertiesSource extends PropertiesPropertySource {
    
    public EncryptPropertiesSource(String name, Properties source) {
        super(name, source);
    }
    
    protected EncryptPropertiesSource(String name, Map<String, Object> source) {
        super(name, source);
    }
}
