package cn.rongcapital.chorus.common.xd.autonconfigure;

import cn.rongcapital.chorus.common.xd.SpringXDTemplateDecorator;
import cn.rongcapital.chorus.common.xd.model.XDAdminConfig;
import cn.rongcapital.chorus.common.zk.ZKClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yimin
 */
@Slf4j
@Configuration
@ConditionalOnMissingBean(SpringXDTemplateDecorator.class)
public class SpringXDTemplateDecoratorAutoConfiguration {

    @Bean
    public SpringXDTemplateDecorator xdTemplateDecorator(XDAdminConfig xdAdminConfig, ZKClient zkClient) {
        return new SpringXDTemplateDecorator(xdAdminConfig, zkClient);
    }
}
