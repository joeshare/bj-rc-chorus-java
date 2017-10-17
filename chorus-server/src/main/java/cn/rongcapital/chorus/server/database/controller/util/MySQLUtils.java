package cn.rongcapital.chorus.server.database.controller.util;

import cn.rongcapital.chorus.common.util.StringUtils;
import cn.rongcapital.chorus.das.entity.ResourceOut;
import cn.rongcapital.chorus.das.entity.TableListDO;
import cn.rongcapital.chorus.server.database.controller.vo.DBVo;
import org.apache.ibatis.jdbc.SQL;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * MySQL 操作工具类
 *
 * @author shicheng
 * @version 1.0
 * @since <pre>十一月 24, 2016</pre>
 */
public class MySQLUtils {

    public static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver"; // 驱动
    private static final String MYSQL_CONNECTION_URL = "jdbc:mysql://%s:%s"; // 连接地址
    public static final String MYSQL_HOST = "localhost"; // 主机名
    public static final String MYSQL_PORT = "3306";
    public static final String MYSQL_USRENAME = "root"; // 用户名
    public static final String MYSQL_PASSWORD = "root"; // 密码

    static final String SHOW_DATABASES = "show databases"; // 显示所有数据库
    static final String SHOW_TABLES = "show tables"; // 显示所有数据表

    static {
        try {
            Class.forName(MYSQL_DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取数据库连接地址
     *
     * @param vo 数据库信息
     * @return 数据库连接地址
     */
    public static String getMysqlConnectionUrl(DBVo vo) {
        if (vo != null) {
            if (StringUtils.isNotEmpty(vo.getHost()) && StringUtils.isNotEmpty(vo.getPort())) {
                return String.format(MYSQL_CONNECTION_URL, vo.getHost(), vo.getPort());
            }
        }
        return null;
    }

    /**
     * 获取数据库连接地址
     *
     * @param vo 数据库信息
     * @return 数据库连接地址
     */
    public static String getMysqlConnectionUrlByDB(DBVo vo) {
        if (vo != null) {
            if (StringUtils.isNotEmpty(vo.getHost()) && StringUtils.isNotEmpty(vo.getPort())) {
                return String.format(MYSQL_CONNECTION_URL, vo.getHost(), vo.getPort()) + "/" + vo.getDatabaseName();
            }
        }
        return null;
    }

    /**
     * 获取 mysql 连接
     *
     * @param vo  数据库信息
     * @param url 连接地址
     * @return 连接
     * @throws SQLException
     */
    public static Connection getMySQLConnection(DBVo vo, String url) throws SQLException {
        Connection connection = null;
        if (vo != null) {
            connection = DriverManager.getConnection(url, vo.getUserName(), vo.getPassword());
        }
        return connection;
    }

    /**
     * 获取 mysql 连接
     *
     * @param userName 用户名
     * @param password 密码
     * @param url      连接地址
     * @return 连接
     * @throws SQLException
     */
    public static Connection getMySQLConnection(String url, String userName, String password) throws SQLException {
        Connection connection = null;
        if (StringUtils.isNotEmpty(url)) {
            connection = DriverManager.getConnection(url, userName, password);
        }
        return connection;
    }

    /**
     * 测试连接
     *
     * @param vo 数据库连接信息
     * @return 连接状态
     * @throws SQLException
     */
    public static boolean testConnection(DBVo vo) throws SQLException {
        Connection connection = getMySQLConnection(vo, getMysqlConnectionUrl(vo));
        if (connection != null) {
            connection.close();
            return true;
        }
        return false;
    }

    /**
     * 获取所有的数据库
     *
     * @param vo 数据库信息
     * @return 数据库集合
     * @throws SQLException
     */
    public static List<String> getAllDatabase(DBVo vo) throws SQLException {
        List<String> list = new ArrayList<>();
        Connection connection = getMySQLConnection(vo, getMysqlConnectionUrl(vo));
        if (connection != null) {
            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery(SHOW_DATABASES);
            while (set.next()) {
                list.add(set.getString(1));
            }
            statement.close();
            set.close();
        }
        connection.close();
        return list;
    }

    /**
     * 根据数据库获取所有的数据表
     *
     * @param vo 数据库信息
     * @return 数据表集合
     * @throws SQLException
     */
    public static List<String> getAllTable(DBVo vo) throws SQLException {
        List<String> list = new ArrayList<>();
        if (checkDbExists(vo)) {
            Connection connection = getMySQLConnection(vo, getMysqlConnectionUrlByDB(vo));
            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery(SHOW_TABLES);
            while (set.next()) {
                list.add(set.getString(1));
            }
            statement.close();
            set.close();
        }
        return list;
    }

    /**
     * 检查数据库是否存在
     *
     * @param vo 数据库信息
     * @return true | false
     */
    public static boolean checkDbExists(DBVo vo) {
        try {
            Connection connection = getMySQLConnection(vo, vo.getUrl());
            connection.close();
            return true;
        } catch (SQLException sqlE) {
            return false;
        }
    }

    /**
     * 根据数据库获取所有的数据表(格式 dabataseName.tableName)
     *
     * @param vo 数据库信息
     * @return 数据表集合
     * @throws SQLException
     */
    public static List<String> getAllTableToDT(DBVo vo) throws SQLException {
        List<String> list = new ArrayList<>();
        if (checkDbExists(vo)) {
            Connection connection = getMySQLConnection(vo, getMysqlConnectionUrlByDB(vo));
            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery(SHOW_TABLES);
            while (set.next()) {
                list.add(String.format("%s.$s", vo.getDatabaseName(), set.getString(1)));
            }
            statement.close();
            set.close();
        }
        return list;
    }

    /**
     * 根据数据库获取所有的数据表(格式 dabataseName.tableName)
     *
     * @param resourceOuts 外部资源信息列表
     * @return 数据表集合
     * @throws SQLException
     */
    public static List<String> getAllTableToDT(List<ResourceOut> resourceOuts) throws SQLException {
        List<String> list = new ArrayList<>();
        for (ResourceOut r : resourceOuts) {
            ResultSet set = getMySQLConnection(r.getConnUrl(), r.getConnUser(), r.getConnPass()).createStatement().executeQuery(SHOW_TABLES);
            while (set.next()) {
                list.add(String.format("%s.%s", r.getDatabaseName(), set.getString(1)));
            }
            set.close();
        }
        return list;
    }

}