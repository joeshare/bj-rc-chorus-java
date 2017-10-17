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

package cn.rongcapital.chorus.modules.rdb2hive;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.xd.module.options.spi.Mixin;
import org.springframework.xd.module.options.spi.ModuleOption;


/**
 * Module options for Sqoop application module.
 *
 * @author maboxiao
 */
@Mixin({})
public class RDB2HiveOptionsMetadata {

	private String command = "";

	private String args = "";
	
	private String runParams = "";

	@NotBlank
	public String getCommand() {
		return command;
	}

	@ModuleOption("the Sqoop command to run")
	public void setCommand(String command) {
		this.command = command;
	}

	@NotBlank
	public String getArgs() {
		return args;
	}

	@ModuleOption("the arguments for the Sqoop command")
	public void setArgs(String args) {
		this.args = args;
	}
	
	public String getRunParams() {
		return runParams;
	}

	@ModuleOption(value="the run parameters for the Sqoop command")
	public void setRunParams(String runParams) {
		this.runParams = runParams;
	}
}
