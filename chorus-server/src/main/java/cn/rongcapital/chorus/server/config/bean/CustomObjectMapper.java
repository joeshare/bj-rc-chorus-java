package cn.rongcapital.chorus.server.config.bean;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;

/**
 * Created by abiton on 22/12/2016.
 */
public class CustomObjectMapper extends ObjectMapper {
    
    public CustomObjectMapper() {
        DefaultSerializerProvider sp = new DefaultSerializerProvider.Impl();
        sp.setNullValueSerializer(new NullSerializer());
        this.setSerializerProvider(sp);
    }
}
