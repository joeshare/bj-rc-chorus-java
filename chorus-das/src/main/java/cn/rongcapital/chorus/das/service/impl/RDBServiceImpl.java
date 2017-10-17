package cn.rongcapital.chorus.das.service.impl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.rongcapital.chorus.common.util.ConnectorInfoUtils;
import cn.rongcapital.chorus.das.service.RDBService;
import cn.rongcapital.chorus.das.entity.FieldBean;
import cn.rongcapital.chorus.common.bean.JdbcConnectParamsBean;
import cn.rongcapital.chorus.das.entity.web.R;

/**
 * RDB SERVICE实现类
 *
 * @author maboxiao
 */
@Service(value = "RDBService")
@Transactional
public class RDBServiceImpl implements RDBService {

	@Override
	public R checkSourceConnect(String dbType, String url, String user, String password) {
		// JDBC连接参数生成
		JdbcConnectParamsBean conParm = ConnectorInfoUtils.getJdbcConnectParams(url, dbType, user, password, "", "");

		try {
			Class.forName(conParm.getDriver());
		} catch (ClassNotFoundException e) {
			return R.falseState("数据库连接驱动类不存在,请检查数据库连接配置是否正确。" + e.toString());
		}

		Connection con = null;
		try {
			con = DriverManager.getConnection(conParm.getUrl(), conParm.getUser(), conParm.getPassword());
			return R.trueState("数据库连接校验成功。");
		} catch (SQLException e) {
			return R.falseState("数据库连接失败，请检查配置是否正确。" + e.toString());
		} finally {
			if (con != null)
				try {
					con.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
	}
	
	@Override
	public List<String> getTableList(String dbType, String url, String user, String password) {
		// JDBC连接参数生成
		JdbcConnectParamsBean conParm = ConnectorInfoUtils.getJdbcConnectParams(url, dbType, user, password, "", "");

		StringBuffer sql = new StringBuffer();
		String tableName = "table_name";
		String getTableListSql = ConnectorInfoUtils.getTableListSql(dbType, conParm.getDbName());
		sql.append(getTableListSql);

		List<String> list = new ArrayList<String>();
		Connection  con = null ;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			con = DriverManager.getConnection(conParm.getUrl(), conParm.getUser(), conParm.getPassword());
			if (null==con)
				return null;
			pstmt = con.prepareStatement(sql.toString());
			rs = pstmt.executeQuery();
			while(rs.next()){
				list.add(rs.getString(tableName));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally{
			if (rs!=null)try{rs.close();}catch(Exception e){e.printStackTrace();}
			if (pstmt!=null)try{pstmt.close();}catch(Exception e){e.printStackTrace();}
			if (con!=null)try{con.close();}catch(Exception e){e.printStackTrace();}
		}
		return list;
	}
	
	@Override
	public List<FieldBean> getFieldList(String dbType, String url, String user, String password, String tableName) {
		// JDBC连接参数生成
		JdbcConnectParamsBean conParm = ConnectorInfoUtils.getJdbcConnectParams(url, dbType, user, password, tableName, "");
		
		List<FieldBean> list = new ArrayList<FieldBean>();
		Connection  con = null ;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		DatabaseMetaData dbMeta = null;
		ResultSet pkRSet = null;
		try {
			con = DriverManager.getConnection(conParm.getUrl(), conParm.getUser(), conParm.getPassword());
			if (null==con)
				return null;
			
			String sql = ConnectorInfoUtils.getFieldListSql(dbType, tableName);

			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i=1;i<=rsmd.getColumnCount();i++){
				FieldBean fieldBean = new FieldBean();
				fieldBean.setName(rsmd.getColumnName(i));
				fieldBean.setLabel(rsmd.getColumnLabel(i));
				fieldBean.setSize(rsmd.getColumnDisplaySize(i));
				fieldBean.setType(String.valueOf(rsmd.getColumnType(i)));
				
				list.add(fieldBean);
			}

			// 主键标识添加
			dbMeta = con.getMetaData(); 
			pkRSet = dbMeta.getPrimaryKeys(null, null, tableName);
			HashSet<String> pkMap = new HashSet<String>();
			while (pkRSet.next()) {
				pkMap.add( String.valueOf(pkRSet.getObject(4)));
			}
			for (FieldBean item : list) {
				if (pkMap.contains(item.getName())) {
					// 表主键
					item.setPrimaryKey(true);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally{
			if (rs!=null)try{rs.close();}catch(Exception e){e.printStackTrace();}
			if (pkRSet!=null)try{pkRSet.close();}catch(Exception e){e.printStackTrace();}
			if (pstmt!=null)try{pstmt.close();}catch(Exception e){e.printStackTrace();}
			if (con!=null)try{con.close();}catch(Exception e){e.printStackTrace();}
		}

		return list;
	}
}
