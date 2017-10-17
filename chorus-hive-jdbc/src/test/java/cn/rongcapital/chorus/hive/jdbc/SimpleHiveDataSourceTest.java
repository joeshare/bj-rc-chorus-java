package cn.rongcapital.chorus.hive.jdbc;

import cn.rongcapital.chorus.hive.jdbc.SimpleHiveDataSource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.util.Properties;

/**
 * Created by hhlfl on 2017-9-7.
 */
public class SimpleHiveDataSourceTest {
    private SimpleHiveDataSource dataSource ;
    @Before
    public void setup(){
        String url = "jdbc:hive2://dl-rc-optd-ambari-master-v-test-1.host.dataengine.com:10000";
//        String url = "jdbc:hive2://dl-rc-optd-ambari-slave-v-test-1.host.dataengine.com:2181,dl-rc-optd-ambari-slave-v-test-2.host.dataengine.com:2181,dl-rc-optd-ambari-slave-v-test-3.host.dataengine.com:2181/chorus_System03;serviceDiscoveryMode=zooKeeper;zooKeeperNamespace=hiveserver2?tez.queue.name=System03";
        String driver = "org.apache.hive.jdbc.HiveDriver";
        dataSource = new SimpleHiveDataSource(driver,url,"huanghailan","");
    }

    @Test
    public void getConnection() throws Exception {
        Connection conn = dataSource.getConnection();
        Assert.assertNotNull(conn);
        conn.close();
    }

    @Test
    public void getConnection1() throws Exception {
        Connection conn = dataSource.getConnection("huanghailan","");
        Assert.assertNotNull(conn);
        conn.close();
    }

    @Test
    public void buildUrl() throws Exception {
        //jdbc:hive2://<zookeeper quorum>/;serviceDiscoveryMode=zooKeeper;zooKeeperNamespace=hiveserver2?tez.queue.name=hive1&hive.server2.thrift.resultset.serialize.in.tasks=true
        Properties sessionProp = new Properties();
        sessionProp.setProperty("aaa","test");

        String url = "jdbc:hive2://dl-rc-optd-ambari-slave-v-test-1.host.dataengine.com:2181,dl-rc-optd-ambari-slave-v-test-2.host.dataengine.com:2181/;serviceDiscoveryMode=zooKeeper;zooKeeperNamespace=hiveserver2?tez.queue.name=huanghailan";
        String actVal = "jdbc:hive2://dl-rc-optd-ambari-slave-v-test-1.host.dataengine.com:2181,dl-rc-optd-ambari-slave-v-test-2.host.dataengine.com:2181/;serviceDiscoveryMode=zooKeeper;zooKeeperNamespace=hiveserver2;aaa=test?tez.queue.name=huanghailan";
        String exp = dataSource.buildUrl(url,sessionProp);
        Assert.assertTrue(exp.equals(actVal));

        url = "jdbc:hive2://dl-rc-optd-ambari-slave-v-test-1.host.dataengine.com:2181,dl-rc-optd-ambari-slave-v-test-2.host.dataengine.com:2181/db?";
        actVal = "jdbc:hive2://dl-rc-optd-ambari-slave-v-test-1.host.dataengine.com:2181,dl-rc-optd-ambari-slave-v-test-2.host.dataengine.com:2181/db;aaa=test?";
        exp = dataSource.buildUrl(url,sessionProp);
        Assert.assertTrue(exp.equals(actVal));


        url = "jdbc:hive2://dl-rc-optd-ambari-slave-v-test-1.host.dataengine.com:2181,dl-rc-optd-ambari-slave-v-test-2.host.dataengine.com:2181/db?";
        exp = dataSource.buildUrl(url,new Properties());
        Assert.assertTrue(exp.equals(url));

        sessionProp.setProperty("database","db1");
        url = "jdbc:hive2://dl-rc-optd-ambari-slave-v-test-1.host.dataengine.com:2181,dl-rc-optd-ambari-slave-v-test-2.host.dataengine.com:2181/db?";
        actVal = "jdbc:hive2://dl-rc-optd-ambari-slave-v-test-1.host.dataengine.com:2181,dl-rc-optd-ambari-slave-v-test-2.host.dataengine.com:2181/db1;aaa=test?";
        exp = dataSource.buildUrl(url,sessionProp);
        Assert.assertTrue(exp.equals(actVal));

        url = "jdbc:hive2://";
        actVal = "jdbc:hive2:///db1;aaa=test";
        exp = dataSource.buildUrl(url,sessionProp);
        Assert.assertTrue(exp.equals(actVal));


    }

}