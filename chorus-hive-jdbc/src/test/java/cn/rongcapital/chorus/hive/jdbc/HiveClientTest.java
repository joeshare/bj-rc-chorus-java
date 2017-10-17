package cn.rongcapital.chorus.hive.jdbc;

import cn.rongcapital.chorus.hive.jdbc.HiveClient;
import cn.rongcapital.chorus.hive.jdbc.Params;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by hhlfl on 2017-9-8.
 */
public class HiveClientTest {

    @Test
    public void getInstance() throws Exception {
        HiveClient client = HiveClient.getInstance();
        Assert.assertNotNull(client);
        client = null;
    }

    @Test
    public void executeHiveSql() throws Exception {
        HiveClient hiveClient = HiveClient.getInstance();
        String url ="jdbc:hive2://dl-rc-optd-ambari-master-v-test-1.host.dataengine.com:10000/chorus_System03";
        String userName="huanghailan";
        String queueName="System03";
        String hql = "select * from t_hhl_test_005 limit 1";
        String exp = hiveClient.executeHiveSql(url, userName, queueName, hql, null, new HiveClient.ResultSetConsumer<String>() {
            @Override
            public String apply(ResultSet rs) throws SQLException {
                while(rs.next()){
                    return rs.getString(1);
                }

                return "";
            }
        });

        Assert.assertNotNull(exp);
    }

    @Test
    public void executeHiveSqlNonUrl() throws Exception {
        HiveClient hiveClient = HiveClient.getInstance();
        String userName="huanghailan";
        String queueName="System03";
        String hql = "select * from chorus_System03.t_hhl_test_005 limit 1";
        String exp = hiveClient.executeHiveSql(userName, queueName, hql, null, new HiveClient.ResultSetConsumer<String>() {
            @Override
            public String apply(ResultSet rs) throws SQLException {
                while(rs.next()){
                    return rs.getString(1);
                }

                return "";
            }
        });

        Assert.assertNotNull(exp);

    }

    @Test
    public void executeHiveSqlNoRes() throws Exception {

        HiveClient hiveClient = HiveClient.getInstance();
        String url ="jdbc:hive2://dl-rc-optd-ambari-master-v-test-1.host.dataengine.com:10000/chorus_System03";
        String userName="huanghailan";
        String queueName="System03";
        String hql = "show tables";
        boolean exp = hiveClient.executeHiveSqlNoRes(url, userName, queueName, hql, null);
        Assert.assertTrue(exp);
    }

    @Test
    public void executeHiveSqlNoResNonUrl() throws Exception {
        HiveClient hiveClient = HiveClient.getInstance();
        String userName="huanghailan";
        String queueName="System03";
        String hql = "show databases";
        boolean exp = hiveClient.executeHiveSqlNoRes(userName, queueName, hql, null);
        Assert.assertTrue(exp);
    }

    @Test
    public void getHiveConn() throws Exception {
        String url ="jdbc:hive2://dl-rc-optd-ambari-master-v-test-1.host.dataengine.com:10000/chorus_System03";
        String userName="huanghailan";
        String queueName="System03";
        Connection conn = HiveClient.getInstance().getHiveConn(url,userName,"",queueName);
        Assert.assertNotNull(conn);
        conn.close();
    }

    @Test
    public void getHiveConn1() throws Exception {
        String userName="huanghailan";
        String queueName="System03";
        Connection conn = HiveClient.getInstance().getHiveConn(userName,"",queueName);
        Assert.assertNotNull(conn);
        conn.close();
    }

    @Test
    public void getHiveConn2() throws Exception {
        Properties sessionProp = new Properties();
        sessionProp.setProperty(Params.PARAM_DATABASE,"chorus_System03");
        Properties connProp = new Properties();
        String userName="huanghailan";
        String queueName="System03";
        connProp.setProperty(Params.PARAM_USER,userName);
        connProp.setProperty(Params.PARAM_TEZ_QUEUE_NAME,queueName);
        Connection conn = HiveClient.getInstance().getHiveConn(sessionProp, connProp);
        Assert.assertNotNull(conn);
        conn.close();
    }


}