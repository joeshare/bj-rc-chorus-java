package cn.rongcapital.chorus.hive.jdbc;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Created by hhlfl on 2017-9-6.
 */
public class HiveClient {
    private static final Log log = LogFactory.getLog(HiveClient.class);
    private static final HiveClient instance = new HiveClient();

    private HiveClient(){}

    public static HiveClient getInstance(){
        return instance;
    }

    public <R> R executeHiveSql(String userName, String queueName,
                                    String hql, String rollbackHql,ResultSetConsumer<R> consumer) {
        return executeHiveSql(null,userName,queueName,hql,rollbackHql, consumer);
    }

    public <R> R executeHiveSql(String url, String userName, String queueName,
                                String hql, String rollbackHql,ResultSetConsumer<R> consumer) {
        return executeHiveSql(url,userName,queueName,null,null,null,hql,rollbackHql,consumer);
    }

    public <R> R executeHiveSql(String url, String userName, String queueName,String password,String database, String principal,
                                String hql, String rollbackHql, ResultSetConsumer<R> consumer) {

        Properties connProp = new Properties();
        Properties sessionProp =new Properties();
        setProperties(connProp,Params.PARAM_USER,userName);
        setProperties(connProp,Params.PARAM_PASSWORD,password);
        setProperties(connProp,Params.PARAM_TEZ_QUEUE_NAME,queueName);
        setProperties(sessionProp,Params.PARAM_DATABASE,database);
        setProperties(sessionProp,Params.PARAM_PRINCIPAL, principal);

        return executeHiveSql(url,sessionProp,connProp,hql,rollbackHql,consumer);
    }



    public <R> R executeHiveSql(String url, Properties sessionProp, Properties connProp,
                                           String hql, String rollbackHql, ResultSetConsumer<R> consumer) {
        return execute(url, sessionProp, connProp, connection -> {
            Statement stmt = null;
            ResultSet rs = null;
            try {
                log.info(String.format("Preparing execute sql: [%s] in hive (with result), rollback: [%s]",
                        hql, rollbackHql));
                stmt = connection.createStatement();
                rs = stmt.executeQuery(hql);
                return consumer.apply(rs);
            }finally {
                close(null, stmt,rs);
            }
        }, connection -> {
            if (rollbackHql != null && rollbackHql.trim().length()>1) {
                log.info(String.format("Preparing rollback for sql: [%s] in hive (without result), rollback: [%s]",
                        hql, rollbackHql));
                Statement rollbackStmt = null;
                try {
                    rollbackStmt = connection.createStatement();
                    rollbackStmt.execute(rollbackHql);
                }finally {
                   close(null,rollbackStmt,null);
                }
            }
        });

    }

    public boolean executeHiveSqlNoRes(String userName, String queueName,
                                           String hql, String rollbackHql) {
        return executeHiveSqlNoRes(null,userName,queueName,hql,rollbackHql);
    }

    public boolean executeHiveSqlNoRes(String url, String userName, String queueName,
                                    String hql, String rollbackHql) {
        return executeHiveSqlNoRes(url,userName,queueName,null,null,null,hql,rollbackHql);
    }
    public boolean executeHiveSqlNoRes(String url, String userName, String queueName,String password, String database, String principal,
                                    String hql, String rollbackHql) {

        Properties connProp = new Properties();
        Properties sessionProp =new Properties();
        setProperties(connProp,Params.PARAM_USER,userName);
        setProperties(connProp,Params.PARAM_PASSWORD,password);
        setProperties(connProp,Params.PARAM_TEZ_QUEUE_NAME,queueName);
        setProperties(sessionProp,Params.PARAM_DATABASE,database);
        setProperties(sessionProp,Params.PARAM_PRINCIPAL, principal);
        return executeHiveSqlNoRes(url,sessionProp, connProp,hql, rollbackHql);
    }

    public boolean executeHiveSqlNoRes(Properties sessionProp, Properties connProp,
                                    String hql, String rollbackHql){
        return executeHiveSqlNoRes(null, sessionProp, connProp, hql, rollbackHql);
    }

    public boolean executeHiveSqlNoRes(String url, Properties sessionProp, Properties connProp,
                                           String hql, String rollbackHql) {
        Boolean f = execute(url, sessionProp, connProp, connection -> {
            Statement stmt = null;
            try {
                log.info(String.format("Preparing execute sql: [%s] in hive (without result), rollback: [%s]",
                        hql, rollbackHql));
                stmt = connection.createStatement();
                stmt.executeUpdate(hql);
                return true;
            }finally {
                close(null, stmt, null);
            }
        }, connection -> {
            Statement rollbackStmt = null;
            try {
                if (rollbackHql != null && rollbackHql.trim().length() > 1) {
                    log.info(String.format("Preparing rollback for sql: [%s] in hive (without result), rollback: [%s]",
                            hql, sessionProp, connProp, rollbackHql));
                    rollbackStmt = connection.createStatement();
                    rollbackStmt.execute(rollbackHql);
                }
            }finally {
                close(null, rollbackStmt, null);
            }
        });

        if(f == null) return false;

        return f;
    }

    public <R> R execute(String userName, String queueName, Function<Connection, R> f,
                                Consumer<Connection> rollbackF) {
        return execute(null,userName, queueName,"","", f, rollbackF);
    }

//    public <R> R execute(String url, String userName, String queueName, Function<Connection, R> f,
//                         Consumer<Connection> rollbackF) {
//        return execute(url, userName, queueName,"","",null, f, rollbackF);
//    }

    public <R> R execute(String url, String userName, String queueName, String password, String database,Function<Connection, R> f,
                         Consumer<Connection> rollbackF){
        return execute(url, userName, queueName,password,database,null,f,rollbackF);
    }

    public <R> R execute(String principal, String queueName, String database, Function<Connection, R> f,
                         Consumer<Connection> rollbackF){

        return execute(null,principal,queueName, database,f,rollbackF);
    }

    public <R> R execute(String url, String principal, String queueName, String database,Function<Connection, R> f,
                         Consumer<Connection> rollbackF){
        return execute(url, null, queueName,null,database,principal,f,rollbackF);
    }

    private <R> R execute(String url, String userName, String queueName, String password, String database, String principal, Function<Connection, R> f,
                         Consumer<Connection> rollbackF){
        Properties connProp = new Properties();
        Properties sessionProp =new Properties();
        setProperties(connProp,Params.PARAM_USER,userName);
        setProperties(connProp,Params.PARAM_PASSWORD,password);
        setProperties(connProp,Params.PARAM_TEZ_QUEUE_NAME,queueName);
        setProperties(sessionProp,Params.PARAM_DATABASE,database);
        setProperties(sessionProp,Params.PARAM_PRINCIPAL, principal);

        return execute(url,sessionProp, connProp, f, rollbackF);
    }

    private void setProperties(Properties properties, String key, String val){
        if(val != null && !"".equals(val))properties.setProperty(key, val);
    }
    public <R> R execute(String url, Properties sessionProp, Properties connProp, Function<Connection, R> f,
                                Consumer<Connection> rollbackF){
        Connection conn = null;
        try {
            conn = getHiveConn(url, sessionProp, connProp);
            return f.apply(conn);
        } catch (Exception e) {
            log.error("Error while executing SQL in Hive!!!", e);
            try {
                if(conn != null && rollbackF != null)
                    rollbackF.apply(conn);
            }catch (Exception ex){
                log.error("roll back sql Exception.",ex);
            }

        } finally {
            close(conn,null,null);
        }

        return null;
    }


    public Connection getHiveConn(Properties sessionProp, Properties connProp) throws SQLException{
        return getHiveConn(null,sessionProp,connProp);
    }

    public Connection getHiveConn(String url, Properties sessionProp, Properties connProp) throws SQLException{
        SimpleHiveDataSource dataSource = DataSourceFactory.getInstance().createDataSource(sessionProp, connProp);
        if(url != null && !"".equals(url)){
            dataSource.setUrl(url);
        }

        return dataSource.getConnection();
    }

    public Connection getHiveConn(String principal, String queueName)throws SQLException{
        Properties connProp = new Properties();
        setProperties(connProp, Params.PARAM_TEZ_QUEUE_NAME,queueName);
        Properties sessionProp = new Properties();
        setProperties(sessionProp, Params.PARAM_PRINCIPAL, principal);
        return DataSourceFactory.getInstance().createDataSource(sessionProp, connProp).getConnection();
    }

    public Connection getHiveConn(String userName, String password, String queueName)throws SQLException{
        return getHiveConn(null,userName,password,queueName);
    }

    /**
     * 获取conn 连接 by 用户
     */
    public Connection getHiveConn(String url, String userName, String password, String queueName) throws SQLException {
        Properties connProp = new Properties();
        connProp.setProperty(Params.PARAM_TEZ_QUEUE_NAME,queueName);
        connProp.setProperty(Params.PARAM_USER,userName);
        connProp.setProperty(Params.PARAM_PASSWORD,password);
        SimpleHiveDataSource dataSource = DataSourceFactory.getInstance().createDataSource(null, connProp);
        if(url != null && !"".equals(url)){
            dataSource.setUrl(url);
        }

        return dataSource.getConnection();
    }


    private void close(Connection conn, Statement st , ResultSet rs){
        try{
            if(rs != null) {
                rs.close();
                rs = null;
            }
        }catch (Exception ex){
            log.error(ex.getMessage());
        }finally {
            try{
                if(st != null && !st.isClosed()){
                    st.close();
                    st = null;
                }
            }catch (Exception ex){
                log.error(ex.getMessage());
            }finally {
                try {
                    if (conn != null && !conn.isClosed()) {
                        conn.close();
                        conn = null;
                    }
                }catch (SQLException ex){
                    log.error(ex.getMessage());
                }
            }
        }

    }

    public static interface Function<T, R> {
        R apply(T t) throws SQLException;
    }

    public static interface Consumer<T> {
        void apply(T t) throws SQLException;
    }

    public static interface ResultSetConsumer<R> {
        R apply(ResultSet rs) throws SQLException;
    }


}
