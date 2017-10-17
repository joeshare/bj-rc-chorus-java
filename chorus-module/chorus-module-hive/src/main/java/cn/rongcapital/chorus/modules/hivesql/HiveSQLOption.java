/*
 * Copyright 2015 the original author or authors.
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

package cn.rongcapital.chorus.modules.hivesql;


import static org.springframework.xd.module.options.spi.page.PageElementType.InputText;
import static org.springframework.xd.module.options.spi.page.PageElementType.TextArea;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.xd.module.options.spi.Mixin;
import org.springframework.xd.module.options.spi.ModuleOption;
import org.springframework.xd.module.options.spi.page.PageElement;
import org.springframework.xd.module.options.spi.page.Variable;

/**
 * @author yy
 */
@Mixin({})
public class HiveSQLOption {

	String sql = "";
	
	String queueName = "";
	
	String serverUrl = "";
	
	String userName = "";
	
	Integer retryCount = 0;

	@PageElement(TextArea)
	@Variable("[{\"name\":\"$executionDate\", \"desc\":\"sql内可用，默认任务执行日期（yyyy-MM-dd）\"}," +
			"{\"name\":\"$sqlVar1\", \"desc\":\"sql内可用变量1\"}," +
			"{\"name\":\"$sqlVar2\", \"desc\":\"sql内可用变量2\"}," +
			"{\"name\":\"$sqlVar3\", \"desc\":\"sql内可用变量3\"}," +
			"{\"name\":\"$sqlVar4\", \"desc\":\"sql内可用变量4\"}," +
			"{\"name\":\"$sqlVar5\", \"desc\":\"sql内可用变量5\"}]")
	@NotNull
	public String getSql() {
		return sql;
	}

	@ModuleOption(value = "hive sql" )
	public void setSql(String sql) {
		this.sql = sql;
	}

	@PageElement(InputText)
	@NotNull
	public String getQueueName() {
		return queueName;
	}

	@ModuleOption(value = "queue name", defaultValue = "default", hidden = true)
	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	@PageElement(InputText)
	@NotNull
	public String getServerUrl() {
		return serverUrl;
	}

	@ModuleOption(value = "hive server url", hidden = true)
	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	@PageElement(InputText)
	@NotNull
	public String getUserName() {
		return userName;
	}

	@ModuleOption(value = "hive user", hidden = true)
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	@PageElement(InputText)
	@Min(0)
    @Max(3)
    public Integer getRetryCount() {
        return retryCount;
    }

    @ModuleOption(value = "retry count", defaultValue = "0")
    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }
}
