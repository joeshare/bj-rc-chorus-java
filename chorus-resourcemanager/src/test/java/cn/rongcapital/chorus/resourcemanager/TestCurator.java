package cn.rongcapital.chorus.resourcemanager;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;

/**
 * Created by abiton on 28/11/2016.
 */
public class TestCurator {
    public static void main(String[] args) throws Exception {
        String connectionString = "10.200.48.66:2181,10.200.48.67:2181,10.200.48.68:2181";
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(connectionString, retryPolicy);
        client.start();

        Stat stat = client.checkExists().forPath("/chorus/hosts");
        System.out.println(stat);

        Stat stat1 = client.checkExists().forPath("/dasks/dasdaf");
        System.out.println(stat1);

        client.close();
    }



}
