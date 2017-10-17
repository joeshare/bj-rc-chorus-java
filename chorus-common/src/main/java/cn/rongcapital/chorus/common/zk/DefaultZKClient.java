package cn.rongcapital.chorus.common.zk;

import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooKeeper.States;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Zookeeper 客户端默认实现
 *
 * @author li.hzh
 * @date 2016-11-14 10:32
 */
@Component
public class DefaultZKClient implements ZKClient, InitializingBean, Watcher {

    private static Logger logger = LoggerFactory.getLogger(DefaultZKClient.class);

    @Autowired
    private ZKConfig zkConfig;
    private ZooKeeper zooKeeper;

    @Override
    public byte[] getData(String path, ZKWatcher zkWatcher) throws KeeperException, InterruptedException {
        return zooKeeper.getData(path, zkWatcher == null ? null : zkWatcher, null);
    }

    @Override
    public List<String> getChildren(String path, ZKWatcher zkWatcher) throws KeeperException, InterruptedException {
        return zooKeeper.getChildren(path, zkWatcher == null ? null : zkWatcher);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        zooKeeper = new ZooKeeper(zkConfig.getAddress(), zkConfig.getTimeout(), this);
        while (States.CONNECTING == zooKeeper.getState()) {
            logger.info("Zookeeper client connecting...");
            Thread.sleep(2000L);
        }
        logger.info("Zookeeper 客户端初始化完成。");
    }

    @Override
    public void process(WatchedEvent event) {
        // nothing to do
    }

}
