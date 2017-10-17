package cn.rongcapital.chorus.server.config;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * zookeeper配置
 * @author kevin.gong
 * @Time 2017年9月27日 下午1:47:53
 */
@Configuration
public class ZookeeperConfig {

    @Value(value = "${zookeeper.address}")
    private String zookeeperAddress;

    @Value(value = "${zookeeper.timeout}")
    private Integer zookeeperTimeout;

    @Value(value = "${monitor.spring.xd.zk.connect.retry.time}")
    private Integer retryTime = 1000;

    @Value(value = "${monitor.spring.xd.zk.connect.retry.count}")
    private Integer retryCount = 5;
    
    @Bean(name = "curator")
    public CuratorFramework curator(){
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(retryTime, retryCount);
        CuratorFramework curator = CuratorFrameworkFactory.builder().connectString(zookeeperAddress)
              .sessionTimeoutMs(zookeeperTimeout).retryPolicy(retryPolicy).build();
        curator.start();
        return curator;
    }
}
