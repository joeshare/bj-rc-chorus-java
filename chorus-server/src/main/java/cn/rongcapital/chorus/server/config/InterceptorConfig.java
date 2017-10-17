package cn.rongcapital.chorus.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import cn.rongcapital.chorus.server.interceptor.RequestPathHandler;

/**
 * @author kevin.gong
 * @Time 2017年9月25日 下午4:33:44
 */
@Configuration
public class InterceptorConfig extends WebMvcConfigurerAdapter {
    
    /**
     * 请求路径处理器
     */
    @Bean
    RequestPathHandler requestPathHandler() {
         return new RequestPathHandler();
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
          //TODO 由于前端有处理，为防止冲突，暂不做拦截
//        registry.addInterceptor(requestPathHandler());
    }

}
