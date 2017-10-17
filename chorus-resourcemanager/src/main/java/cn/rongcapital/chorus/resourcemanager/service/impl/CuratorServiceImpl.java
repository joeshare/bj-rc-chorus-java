package cn.rongcapital.chorus.resourcemanager.service.impl;

import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.common.exception.ServiceException;
import cn.rongcapital.chorus.resourcemanager.service.CuratorCallBack;
import cn.rongcapital.chorus.resourcemanager.service.CuratorService;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Created by abiton on 27/11/2016.
 */
@Service
@Slf4j
public class CuratorServiceImpl implements CuratorService, InitializingBean, DisposableBean {
    @Value("${zookeeper.address}")
    String connectString;
    @Value("${zookeeper.timeout}")
    int timeout;
    @Value("${zk.chorus.host.path}}")
    String hostPath;
    CuratorFramework client;

    @Override
    public CuratorFramework getClient() {
        return client;
    }

    @Override
    public void executeWithLock(CuratorCallBack callBack) {
        InterProcessLock lock = new InterProcessMutex(client, hostPath);

        try {
            if (!lock.acquire(60L, TimeUnit.SECONDS)) {
                throw new ServiceException(StatusCode.ZK_CANNOT_GETLOCK_ERROR);
            }
        } catch (Exception e) {
            log.error("acquire zookeeper lock error ", e);
            throw new ServiceException(StatusCode.ZK_CANNOT_GETLOCK_ERROR);
        }

        try {
            callBack.run(getClient());
        } catch (ServiceException se) {
            log.warn("execute callback error", se);
            throw se;
        } catch (Exception e) {
            log.error("execute callback error",e);
            throw new ServiceException(StatusCode.SYSTEM_ERR);
        } finally {
            try {
                lock.release();
            } catch (Exception e) {
                log.error("release zookeeper lock error ", e);
            }
        }

    }


    @Override
    public void destroy() throws Exception {
        client.close();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        client = CuratorFrameworkFactory.newClient(connectString, new ExponentialBackoffRetry(timeout, 3));
        client.start();
    }
}
