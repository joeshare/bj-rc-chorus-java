package cn.rongcapital.chorus.common.zk;

import org.apache.zookeeper.KeeperException;

import java.util.List;

/**
 * Zookeeper 客户端
 *
 * @author li.hzh
 * @date 2016-11-14 10:19
 */
public interface ZKClient {
    
    /**
     * 获取Zookeeper节点上数据
     *
     * @param path      节点路径
     * @param zkWatcher 监听器
     * @return
     * @throws KeeperException
     * @throws InterruptedException
     */
    byte[] getData(String path, ZKWatcher zkWatcher) throws KeeperException, InterruptedException;
    
    /**
     * 获取子节点列表
     *
     * @param path 节点路径
     * @return
     */
    List<String> getChildren(String path, ZKWatcher zkWatcher) throws KeeperException, InterruptedException;
    
}
