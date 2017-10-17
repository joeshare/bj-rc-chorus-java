package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.common.exception.ServiceException;
import cn.rongcapital.chorus.das.dao.SqlQueryMapper;
import cn.rongcapital.chorus.das.entity.CalculateSQL;
import cn.rongcapital.chorus.das.entity.ExecuteHistory;
import cn.rongcapital.chorus.das.entity.web.ExecuteHistoryCause;
import cn.rongcapital.chorus.das.service.QueueNameService;
import cn.rongcapital.chorus.das.service.SqlQueryService;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.stat.TableStat.Name;
import com.alibaba.druid.util.JdbcUtils;
import cn.rongcapital.chorus.hive.jdbc.HiveClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service(value="SqlQueryService")
public class SqlQueryServiceImpl implements SqlQueryService {

	private final Logger log = LoggerFactory.getLogger(SqlQueryServiceImpl.class);

	public static final String HIVE_DRIVER = "org.apache.hive.jdbc.HiveDriver";
	
	public static final String PROSTO_DRIVER = "com.facebook.presto.jdbc.PrestoDriver";
	
	@Autowired
	private SqlQueryMapper mapper;
	
    @Autowired
    private QueueNameService queueNameService;
			
	@Override
	public List<LinkedHashMap<String, String>> calculate(CalculateSQL calculateSQL, String userId) throws Exception{
		
		String sql = calculateSQL.getSql();

		if (StringUtils.isBlank(sql)) {
			throw new ServiceException(StatusCode.SQLQUERY_SQL_BLANK);
		}

		// 获取执行queue
//		String projectCode = getProjectCode(sql);
		String projectCode = calculateSQL.getCurrentProjectCode();

		if (StringUtils.isBlank(projectCode)) {
//			throw new ServiceException(StatusCode.SQLQUERY_SQL_ILLEGAL);
			projectCode = "default";
		}

		ExecuteHistory executeHistory = new ExecuteHistory();
		executeHistory.setExecuteSql(sql);
		executeHistory.setExecuteStatus(0);
		executeHistory.setExecuteTime(null);
		executeHistory.setCreateUser(userId);
		executeHistory.setCreateTime(new Date());
		executeHistory.setUpdateUser(userId);
		mapper.insert(executeHistory);
		
		long execStartTime = System.currentTimeMillis();

		Vector<String> vstrError = new Vector<String>();

//		String strDataBase = "default";
		
		// 检索记录条数限制
		String reg = "\\blimit\\b\\s+(\\d+)\\b$"; 
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(sql); 
		String hSql = " select * from (" + sql + ") t_ttt limit " + SqlQueryService.QUERY_LIMIT_DEFAULT + " ";
		if(m.find()) {
			int limitCount = Integer.parseInt(m.group(1));
			if (limitCount <= SqlQueryService.QUERY_LIMIT_MAX) {
				hSql = sql;
			}
		}

		String queue = queueNameService.projectQueue(projectCode);

//		String url = connectorConfigInfo.getPrestoServerUrl() + strDataBase;
//		if (!StringUtils.isBlank(queue)) {
//			url = url + "?tez.queue.name=" + queue;
//		}
		List<LinkedHashMap<String, String>> result = calculate(hSql, vstrError, userId, queue, "default");

		if (vstrError.size() > 0) {
			executeHistory.setExecuteStatus(-1);
			executeHistory.setExecuteTime(System.currentTimeMillis() - execStartTime);
			mapper.updateStatusAndTime(executeHistory);
			String errMsg = vstrError.get(0);
			throw handleErrMsg(errMsg);
		} else {
			executeHistory.setExecuteStatus(1);
			executeHistory.setExecuteTime(System.currentTimeMillis() - execStartTime);
			mapper.updateStatusAndTime(executeHistory);
		}

		return result;
	}

	private ServiceException handleErrMsg(String errMsg) {
		ServiceException e;
		String msg = errMsg != null ? errMsg.toLowerCase() : "";
		log.info("Hive SQL Error message:{}", msg);
		if (msg.contains("table not found")) {
			e = new ServiceException(StatusCode.SQLQUERY_SQL_TABLE_NOT_FOUND);
		} else if (msg.contains("invalid table alias or column reference") || msg.contains("invalid column reference")) {
			e = new ServiceException(StatusCode.SQLQUERY_SQL_COLUMN_INVALID);
		} else if (msg.contains("parseexception")) {
			e = new ServiceException(StatusCode.SQLQUERY_SQL_ILLEGAL);
		} else if (msg.contains("hiveaccesscontrolexception")) {
			e = new ServiceException(StatusCode.SQLQUERY_SQL_PERMISSION_DENIED);
		} else {
			e = new ServiceException(StatusCode.SQLQUERY_QUERY_ERROR);
		}
		return e;
	}
	
	// 从sql中取得projectCode
	private String getProjectCode(String sql) {

		try {
			MySqlStatementParser parser = new MySqlStatementParser(sql.toLowerCase());

			SQLSelectStatement statement = parser.parseSelect();

			SchemaStatVisitor schemaStatVisitor = SQLUtils.createSchemaStatVisitor(JdbcUtils.MYSQL);
			statement.accept(schemaStatVisitor);

			Map<Name, TableStat> tables = schemaStatVisitor.getTables();

			if (tables.size() == 0) {
				return "";
			}

			for (Name name : tables.keySet()) {
				String[] tableName = name.getName().split("\\.");
				if (tableName.length < 2) {
					return "";
				} else {
					return tableName[0].trim().startsWith("chorus_")?tableName[0].trim().replaceFirst("chorus_", ""):tableName[0].trim();
				}
			}
		} catch (Exception e) {
			log.error("",e);
			return "";
		}

		return "";
	}
	
	// 通过JDBC执行
    private List<LinkedHashMap<String, String>> calculate(String strHadoopSql, Vector<String> vstrError, String strUserId, String queueName, String database) {
        log.info("ExecuteUser:" + strUserId);
        log.info("ExecuteSQL:" + strHadoopSql);
        return HiveClient.getInstance().execute(null,strUserId, queueName, "",database,new HiveClient.Function<Connection, List<LinkedHashMap<String, String>>>() {
            @Override
            public List<LinkedHashMap<String, String>> apply(Connection conn) throws SQLException {
                List<LinkedHashMap<String, String>> result = new ArrayList<LinkedHashMap<String, String>>();
                ResultSet res = null;
                Statement kState = null;
                try {
                    kState = conn.createStatement();
                    kState.setFetchSize(1500);
                    res = kState.executeQuery(strHadoopSql);

                    int icount = res.getMetaData().getColumnCount();
                    ArrayList<String> listColumnName = new ArrayList<String>();
                    ArrayList<String> listColumnNameNew = new ArrayList<String>();
                    for (int i = 0; i < icount; i++) {
                        String columnName = res.getMetaData().getColumnName(i + 1);

                        if (columnName.indexOf(".") != -1) {
                            columnName = columnName.split("\\.")[1];
                        }

                        listColumnName.add(res.getMetaData().getColumnName(i + 1));
                        listColumnNameNew.add(columnName);
                    }

                    while (res.next()) {
                        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
                        for (int i = 0; i < listColumnName.size(); i++) {
                            map.put(listColumnNameNew.get(i), res.getString(listColumnName.get(i)));
                        }
                        if (map.size() > 0) {
                            result.add(map);
                        }
                    }

                }catch (Exception ex){
                    vstrError.add(ex.getMessage());
                    throw ex;
                }finally {
                    if(kState != null)
                        try{kState.close();}catch (SQLException ex){}
                    if(res != null)
                        try{res.close();}catch (SQLException ex){}
                }

                return result;
            }
        },null);
    }

	@Override
	public int count(ExecuteHistoryCause cause) {
        int count = mapper.count(cause);
        return count;
	}

	@Override
	public List<ExecuteHistory> list(ExecuteHistoryCause cause) {
        return mapper.list(cause);
	}

}
