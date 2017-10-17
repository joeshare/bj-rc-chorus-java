package cn.rongcapital.chorus.modules.resource.kpi.snapshot;


import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by hhlfl on 2017-7-18.
 */
public class DataSourceTest extends TestConfig {

    @Test
    public void getConnection(){
        try {
            Connection conn = ds.getConnection();
            conn.close();
            Assert.assertTrue(true);
        } catch (SQLException e) {
            Assert.assertTrue(false);
        }
    }
}
