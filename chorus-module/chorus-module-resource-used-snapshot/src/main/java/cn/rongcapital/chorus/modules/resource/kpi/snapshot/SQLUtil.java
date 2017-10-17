package cn.rongcapital.chorus.modules.resource.kpi.snapshot;

import lombok.extern.slf4j.Slf4j;

import java.sql.*;

/**
 * Created by hhlfl on 2017-7-14.
 */
@Slf4j
public class SQLUtil {
    public static <T> T query(DataSource dataSource, ResultSolver<T> resultSolver, String sql) throws SQLException{
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        try{
            conn = dataSource.getConnection();
            st = conn.createStatement();
            rs = st.executeQuery(sql);
            return resultSolver.result(rs);
        }finally {
            if(rs != null)
                try{
                    rs.close();
                    log.debug("resultSet close success.");
                }catch (Exception e){}

            if(st != null)
                try {
                    st.close();
                    log.debug("statement close success.");
                } catch (SQLException e) {}

            if(conn != null)
                try {
                    conn.close();
                    log.info("connection close success.");

                } catch (SQLException e) {}
        }
    }

    public static <T> void execute(DataSource dataSource, String sql, Function<T> fun, T data) throws SQLException{
        Connection conn = null;
        PreparedStatement st = null;
        try{
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            st = conn.prepareStatement(sql);
            fun.prepared(data,st);
            conn.commit();
        }catch (SQLException ex){
            if(conn != null) {
                conn.rollback();
                log.info("Transactional rollback.");
            }

            throw ex;
        }finally {
            if(st != null)
                try {
                    st.close();
                    log.debug("preparedStatement close success.");
                } catch (SQLException e) {}

            if(conn != null)
                try {
                    conn.close();
                    log.info("connection close success.");

                } catch (SQLException e) {}
        }
    }
}
