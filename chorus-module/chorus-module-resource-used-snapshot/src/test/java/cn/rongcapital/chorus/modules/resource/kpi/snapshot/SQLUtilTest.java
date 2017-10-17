package cn.rongcapital.chorus.modules.resource.kpi.snapshot;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hhlfl on 2017-7-18.
 */
@Slf4j
public class SQLUtilTest extends TestConfig {

    @Test
    public void query (){
        String sql = "select 'test' as name from dual";
        try {
            String result = SQLUtil.query(ds, new ResultSolver<String>() {
                @Override
                public String result(ResultSet rs) throws SQLException {
                    while (rs.next()){
                        return rs.getString("name");
                    }
                    return "";
                }
            }, sql);
            Assert.assertEquals("test",result);
        }catch (Exception ex){
            log.error(ex.getMessage());
            Assert.assertTrue(false);
        }
    }

    @Test
    public void execute(){
        String sql = "replace into project_resource_kpi_snapshot (project_id,project_name,kpi_date,create_time)" +
                "values(?,?,?,?)";
        List<Object[]> data = new ArrayList<Object[]>();
        Object[] record = new Object[4];
        record[0]=1l;
        record[1]="test_p_01";
        record[2]=new Date(System.currentTimeMillis());
        record[3]=new Timestamp(System.currentTimeMillis());
        data.add(record);
        try {
            SQLUtil.execute(ds, sql, new Function<List<Object[]>>() {
                @Override
                public void prepared(List<Object[]> records, PreparedStatement pst) throws SQLException {
                    for(Object[] record: records){
                        pst.setLong(1,(long)record[0]);
                        pst.setString(2,(String) record[1]);
                        pst.setDate(3,(Date)record[2]);
                        pst.setTimestamp(4,(Timestamp)record[3]);
                        pst.addBatch();
                    }
                    pst.executeBatch();
                }
            }, data);

            sql = String.format("select project_id,project_name,kpi_date,create_time from project_resource_kpi_snapshot where project_name='%s'",record[1]);

            Object[] result = SQLUtil.query(ds, new ResultSolver<Object[]>() {

                @Override
                public Object[] result(ResultSet rs) throws SQLException {
                    Object[] result = new Object[4];
                    while(rs.next()){
                        result[0]=rs.getLong(1);
                        result[1]=rs.getString(2);
                        result[2]=rs.getDate(3);
                        result[3]=rs.getTimestamp(4);
                    }

                    return result;
                }
            }, sql);

            Assert.assertEquals(record[0],result[0]);
            Assert.assertEquals(record[1],result[1]);
            Assert.assertEquals(record[2].toString(),result[2].toString());
        }catch (Exception ex){
            log.error(ex.getMessage());
            Assert.assertTrue(false);
        }
    }
}
