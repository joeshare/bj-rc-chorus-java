package cn.rongcapital.chorus.resourcemanager;

import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by abiton on 09/12/2016.
 */
@Component
public class YarnClientFactoryBean implements InitializingBean, DisposableBean, FactoryBean<YarnClient> {


    private YarnClient yarnClient;

    @Override
    public void destroy() throws Exception {
        yarnClient.close();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        YarnConfiguration configuration = new YarnConfiguration();
        yarnClient = YarnClient.createYarnClient();
        yarnClient.init(configuration);
        yarnClient.start();
    }

    @Override
    public YarnClient getObject() throws Exception {
        return yarnClient;
    }

    @Override
    public Class<?> getObjectType() {
        return YarnClient.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
