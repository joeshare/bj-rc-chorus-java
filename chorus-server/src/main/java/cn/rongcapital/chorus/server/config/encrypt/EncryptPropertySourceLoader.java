package cn.rongcapital.chorus.server.config.encrypt;

import cn.rongcapital.chorus.common.util.CodecUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;
import java.util.Set;

/**
 * @author li.hzh
 * @date 2017-04-10 13:46
 */
@Slf4j
public class EncryptPropertySourceLoader implements PropertySourceLoader, PriorityOrdered {
    
    private static final String ENC_PREFIX = "ENC@[";
    private static final String ENC_POSTFIX = "]";
    
    @Override
    public String[] getFileExtensions() {
        return new String[]{"properties"};
    }
    
    @Override
    public PropertySource<?> load(String name, Resource resource, String profile) throws IOException {
        if (profile == null) {
            Properties properties = PropertiesLoaderUtils
                                            .loadProperties(new EncodedResource(resource, "UTF-8"));
            if (!properties.isEmpty()) {
                decryptValue(properties);
                return new EncryptPropertiesSource(name, properties);
            }
        }
        return null;
    }
    
    private void decryptValue(Properties properties) {
        Set<Object> keys = properties.keySet();
        for (Object key : keys) {
            String value = (String) properties.get(key);
            if (isMatchEncrypt(value)) {
                String finalValue = value;
                try {
                    finalValue = decrypt(value);
                } catch (Exception e) {
                    log.error("Decrypt value [" + value + "] error", e);
                }
                properties.put(key, finalValue);
            }
        }
    }
    
    private boolean isMatchEncrypt(String value) {
        return value.startsWith(ENC_PREFIX) && value.endsWith(ENC_POSTFIX);
    }
    
    private String decrypt(String input) throws Exception {
        String encryptData = getEncryptData(input);
        return CodecUtil.decrypt(encryptData);
    }
    
    private String getEncryptData(String input) {
        return input.substring(ENC_PREFIX.length(), input.length() - ENC_POSTFIX.length());
    }
    
    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }
}
