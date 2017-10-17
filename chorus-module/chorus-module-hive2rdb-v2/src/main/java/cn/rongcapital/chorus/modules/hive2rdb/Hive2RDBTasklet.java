/*
 * Copyright 2014-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.rongcapital.chorus.modules.hive2rdb;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;


import org.apache.commons.lang.StringUtils;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

/**
 * Tasklet used for running Sqoop tool.
 *
 * @author maboxiao
 */
public class Hive2RDBTasklet implements Tasklet {
	
	private String[] arguments;
	
	public String[] getArguments() {
		return arguments;
	}

	public void setArguments(String[] arguments) {
		this.arguments = Arrays.copyOf(arguments, arguments.length);
	}
	
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

		StepExecution stepExecution = chunkContext.getStepContext().getStepExecution();

//        if (arguments.length != 10) {
//            throw new Exception("all parameter must not be blank");
//        }

		String hiveServerUrl = arguments[0];
		String dbName = arguments[1];
		String hiveUser = arguments[2];
		String hiveLocation = arguments[3];
		String rdbConnectUrl = arguments[4];
		String rdbUser = arguments[5];
		String rdbPassword = arguments[6];
		String rdbTable = arguments[7];
		String hiveTable = arguments[8];
		String hiveColumns = arguments[9];
		String where = arguments[10];

		// 第一步建立文本格式的Hive临时表
		String tableFullName = dbName + "." + hiveTable;
		String postfixStr = String.valueOf(System.currentTimeMillis());
//		String postfixStr = "_iamatemptableandhavealongword";
		String tempTableName = hiveTable + "_" + postfixStr;
		String tempTableFullName = dbName + "." + tempTableName;


		// 建表操作
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE ");
		sb.append(tempTableFullName);
		sb.append(" LIKE ").append(tableFullName);
		sb.append(" STORED AS TEXTFILE ");
		sb.append(" LOCATION ").append("'").append(hiveLocation).append(tempTableName).append("'");
		String createTempTableSql = sb.toString();
		stepExecution.getExecutionContext().putString("Create temp table SQL:", createTempTableSql);

		try {
			HiveConnector.excuteHiveSqlNoRes(dbName, createTempTableSql, hiveServerUrl, hiveUser);
		} catch (Exception e) {
			stepExecution.getExecutionContext().putString("Create temp table Error", e.getMessage());
			throw e;
		}

		// 第二步写入临时Hive表，先取得正式表字段元数据
		sb = new StringBuilder();
		sb.append("SELECT * FROM ");
		sb.append(tableFullName);
		sb.append(" LIMIT 1");
		String getMetaDataFromTableSql = sb.toString();
		stepExecution.getExecutionContext().putString("Get MetaData from hive table SQL:", getMetaDataFromTableSql);
		String selectColumns = "";
		try {
			ResultSet resultSet = HiveConnector.excuteHiveSql(dbName, getMetaDataFromTableSql, hiveServerUrl, hiveUser);
			int iCount = resultSet.getMetaData().getColumnCount();
			for (int i = 0; i < iCount; i++) {
				String columnName = resultSet.getMetaData().getColumnName(i + 1);

				if (columnName.indexOf(".") != -1) {
					columnName = columnName.split("\\.")[1];
				}
				if (!"p_date".equalsIgnoreCase(columnName)) {
					selectColumns += columnName + ",";
				}
			}
			if (selectColumns.endsWith(",")) {
				selectColumns = selectColumns.substring(0, selectColumns.length() - 1);
			}
			stepExecution.getExecutionContext().putString("Hive table's columns", selectColumns);
		} catch (Exception e) {
			deleteTempTable(tempTableFullName, dbName,hiveServerUrl,hiveUser,stepExecution);
			stepExecution.getExecutionContext().putString("Get MetaData Error", e.getMessage());
			throw e;
		}

		// 从临时表导入正式表
		Date today = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sb = new StringBuilder();
		sb.append("INSERT OVERWRITE TABLE ");
		sb.append(tempTableFullName);
		sb.append(" PARTITION (p_date='").append(sdf.format(today)).append("')");
		sb.append(" SELECT ").append(selectColumns);
		sb.append(" FROM ").append(tableFullName);

		String copyFromTempTableSql = sb.toString();

		stepExecution.getExecutionContext().putString("Copy temp table data SQL:", copyFromTempTableSql);

		try {
			HiveConnector.excuteHiveSqlNoRes(dbName, copyFromTempTableSql, hiveServerUrl, hiveUser);
		} catch (Exception e) {
			deleteTempTable(tempTableFullName, dbName,hiveServerUrl,hiveUser,stepExecution);
			stepExecution.getExecutionContext().putString("Copy temp table data Error", e.getMessage());
			throw e;
		}

		// 第三步通过SQOOP导临时表数据到RDB
		sb = new StringBuilder();
		sb.append("sqoop export");
		if (rdbConnectUrl.startsWith("jdbc:mysql")) {
			rdbConnectUrl = rdbConnectUrl + "?useUnicode=true&characterEncoding=utf-8";
		}
		sb.append(" --connect '").append(rdbConnectUrl).append("'");
		sb.append(" --username '").append(rdbUser).append("'");
		sb.append(" --password '").append(rdbPassword).append("'");
		sb.append(" --table '").append(rdbTable).append("'");
		sb.append(" --columns '").append(hiveColumns).append("'");
		String exportDir = hiveLocation + tempTableName + "/*";
		sb.append(" --export-dir '").append(exportDir).append("'");
		sb.append(" --input-fields-terminated-by '").append("\\001").append("'");
		if (StringUtils.isNotBlank(where)) sb.append(" --where '").append(where).append("'");

		sb.append(" --m 1");

		String command = sb.toString();
		stepExecution.getExecutionContext().putString("Sqoop Command", command);
		LocalShellTool local = new LocalShellTool();
		Map<String, String> mapResult = local.exec(command);
		if (mapResult != null) {
			String strResult = mapResult.get(LocalShellTool.RESULT_KEY);
			String strError = mapResult.get(LocalShellTool.ERROR_KEY);
			int exitValue = Integer.parseInt(mapResult.get(LocalShellTool.EXITVALUE_KEY));
			stepExecution.getExecutionContext().putString("Sqoop ExitValue", String.valueOf(exitValue));
			stepExecution.getExecutionContext().putString("Sqoop Result", strResult);
			stepExecution.getExecutionContext().putString("Sqoop Error", strError);
			if (exitValue != 0) {
				deleteTempTable(tempTableFullName, dbName,hiveServerUrl,hiveUser,stepExecution);
				throw new Exception(strError);
			}
		} else {
			deleteTempTable(tempTableFullName, dbName,hiveServerUrl,hiveUser,stepExecution);
			throw new Exception("Error");
		}

		// 最后删除临时表操作
		deleteTempTable(tempTableFullName, dbName,hiveServerUrl,hiveUser,stepExecution);

		return RepeatStatus.FINISHED;
	}

	private void deleteTempTable(String tempTableFullName, String dbName, String hiveServerUrl, String hiveUser, StepExecution stepExecution) {
		// 删除临时表操作
		StringBuilder sb = new StringBuilder();
		sb.append("DROP TABLE " ).append(tempTableFullName);
		String dropTempTableSql = sb.toString();
		stepExecution.getExecutionContext().putString("Drop temp table SQL:", dropTempTableSql);

		try {
			HiveConnector.excuteHiveSqlNoRes(dbName, dropTempTableSql, hiveServerUrl, hiveUser);
		} catch (Exception e) {
			stepExecution.getExecutionContext().putString("Drop temp table Error", e.getMessage());
		}
	}
}
