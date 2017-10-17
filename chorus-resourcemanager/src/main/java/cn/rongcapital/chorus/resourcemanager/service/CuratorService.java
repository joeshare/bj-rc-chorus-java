package cn.rongcapital.chorus.resourcemanager.service;

import org.apache.curator.framework.CuratorFramework;

/**
 * Created by abiton on 27/11/2016.
 */
public interface CuratorService {
    CuratorFramework getClient();
    void executeWithLock(CuratorCallBack callBack);
}
