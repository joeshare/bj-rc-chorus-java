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

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.xd.module.options.spi.Mixin;
import org.springframework.xd.module.options.spi.ModuleOption;


/**
 * Module options for Sqoop application module.
 *
 * @author maboxiao
 */
@Mixin({})
public class Hive2RDBOptionsMetadata {

	private String hiveServerUrl = "";

	private String dbName = "";

	private String hiveUser = "";

	private String hiveLocation = "";

	private String rdbConnectUrl = "";

	private String rdbUser = "";

	private String rdbPassword = "";

	private String rdbTable = "";

	private String hiveTable = "";

	private String hiveColumns = "";

	private String where = "";

	@NotBlank
	public String getHiveServerUrl() {
		return hiveServerUrl;
	}

	@ModuleOption(value = "hive server url")
	public void setHiveServerUrl(String hiveServerUrl) {
		this.hiveServerUrl = hiveServerUrl;
	}

	@NotBlank
	public String getDbName() {
		return dbName;
	}

	@ModuleOption(value = "database name")
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	@NotBlank
	public String getHiveUser() {
		return hiveUser;
	}

	@ModuleOption(value = "hive user")
	public void setHiveUser(String hiveUser) {
		this.hiveUser = hiveUser;
	}

	@NotBlank
	public String getHiveLocation() {
		return hiveLocation;
	}

	@ModuleOption(value = "hive location")
	public void setHiveLocation(String hiveLocation) {
		this.hiveLocation = hiveLocation;
	}

	@NotBlank
	public String getRdbConnectUrl() {
		return rdbConnectUrl;
	}

	@ModuleOption(value = "rdb connect url")
	public void setRdbConnectUrl(String rdbConnectUrl) {
		this.rdbConnectUrl = rdbConnectUrl;
	}

	@NotBlank
	public String getRdbUser() {
		return rdbUser;
	}

	@ModuleOption(value = "rdb user")
	public void setRdbUser(String rdbUser) {
		this.rdbUser = rdbUser;
	}

	@NotBlank
	public String getRdbPassword() {
		return rdbPassword;
	}

	@ModuleOption(value = "rdb password")
	public void setRdbPassword(String rdbPassword) {
		this.rdbPassword = rdbPassword;
	}

	@NotBlank
	public String getRdbTable() {
		return rdbTable;
	}

	@ModuleOption(value = "rdb table")
	public void setRdbTable(String rdbTable) {
		this.rdbTable = rdbTable;
	}

	@NotBlank
	public String getHiveTable() {
		return hiveTable;
	}

	@ModuleOption(value = "hive table")
	public void setHiveTable(String hiveTable) {
		this.hiveTable = hiveTable;
	}

	@NotBlank
	public String getHiveColumns() {
		return hiveColumns;
	}

	@ModuleOption(value = "hive columns")
	public void setHiveColumns(String hiveColumns) {
		this.hiveColumns = hiveColumns;
	}

	public String getWhere() {
		return where;
	}

	@ModuleOption(value = "where")
	public void setWhere(String where) {
		this.where = where;
	}
}


