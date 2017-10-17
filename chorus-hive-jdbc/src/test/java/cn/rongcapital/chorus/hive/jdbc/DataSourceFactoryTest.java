package cn.rongcapital.chorus.hive.jdbc;

import cn.rongcapital.chorus.hive.jdbc.DataSourceFactory;
import org.junit.Assert;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Properties;

/**
 * Created by hhlfl on 2017-9-7.
 */
public class DataSourceFactoryTest {
    @Test
    public void createDataSource() throws Exception {
        DataSource dataSource = DataSourceFactory.getInstance().createDataSource();
        Assert.assertNotNull(dataSource);
    }

    @Test
    public void createDataSource1() throws Exception {
        DataSource dataSource = DataSourceFactory.getInstance().createDataSource("huanghailan","");
        Assert.assertNotNull(dataSource);
        Connection conn = dataSource.getConnection();
        Assert.assertNotNull(conn);
        conn.close();
    }

    @Test
    public void createDataSource2() throws Exception {
        Properties connProp = new Properties();
        Properties sessionProp = new Properties();
        connProp.setProperty("user","huanghailan");
        connProp.setProperty("password","");
        connProp.setProperty("tez.queue.name","System03");

        DataSource dataSource = DataSourceFactory.getInstance().createDataSource(sessionProp,connProp);
        Assert.assertNotNull(dataSource);
        Connection conn = dataSource.getConnection();
        Assert.assertNotNull(conn);
        conn.close();
    }

}