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

import java.util.Arrays;
import java.util.Map;

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
		
		if (arguments.length != 3) {
			throw new Exception("Command And Arguments must not be blank");
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("sqoop").append(" ");
		sb.append(arguments[0]).append(" ");
		sb.append(arguments[2]).append(" ");
		sb.append(arguments[1]);
		
//		String command = "sqoop import -D mapreduce.map.memory.mb=1024 -D mapreduce.map.java.opts=-Xmx768m --connect jdbc:mysql://10.200.32.83:3306/chorus --username chorus --password 'adM1n(horus' --table job --hive-import --hive-database chorus_mbxtest1 --hive-table mbxtest1 --hive-overwrite --hive-partition-key p_date --hive-partition-value 20170103 --delete-target-dir --columns job_id,job_alias_name,job_name,status --m 1";
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
            	throw new Exception(strError);
            }
        } else {
        	throw new Exception("Error");
        }
		
		return RepeatStatus.FINISHED;

	}
}
