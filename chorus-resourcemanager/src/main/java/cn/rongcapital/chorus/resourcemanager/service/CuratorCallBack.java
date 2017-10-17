package cn.rongcapital.chorus.resourcemanager.service;

import org.apache.curator.framework.CuratorFramework;

/**
 * Created by abiton on 28/11/2016.
 */
public interface CuratorCallBack {
    void run(CuratorFramework client);
}
