package cn.rongcapital.chorus.modules.hivesql;

import cn.rongcapital.chorus.hive.jdbc.HiveClient;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import cn.rongcapital.chorus.modules.utils.retry.SimpleTasklet;
import cn.rongcapital.log.JobListenerForLog;
import lombok.extern.slf4j.Slf4j;

/**hiveExJob
 * @author maboxiao
 */
@Slf4j
@Configuration
@EnableBatchProcessing
@ComponentScan(basePackages = "cn.rongcapital.chorus.modules.hivesql")
public class HiveSQLJob {

	private static Logger logger = LoggerFactory.getLogger(HiveSQLJob.class);

	@Autowired
	private JobBuilderFactory jobs;

	@Autowired
	private StepBuilderFactory steps;

	@Bean
	public JobExecutionListener logListener() {
		return new JobListenerForLog();
	}

	@Bean
	public Job job(@Qualifier("hiveSQLExecute") Step step1,
				   @Qualifier("logListener") JobExecutionListener logListener) {
		return jobs.get("hiveSQLJob").listener(logListener).start(step1).build();
	}

	/**
	 * module 业务处理步骤
	 * @param sql
	 * @param queueName
	 * @param serverUrl
	 * @param userName
	 * @return
	 */
	@Bean
	protected Step hiveSQLExecute(@Value("${sql}") final String sql,
						          @Value("${queueName}") final String queueName,
						          @Value("${serverUrl}") final String serverUrl,
						          @Value("${userName}") final String userName,
						          @Value("${retryCount:}") Integer retryCount) {

		return steps.get("hiveSQLExecute").tasklet(new SimpleTasklet() {
            @Override
            public RepeatStatus execute(ChunkContext chunkContext, StepContribution contribution) throws Exception {

				final JobParameters jobParameters = chunkContext.getStepContext().getStepExecution().getJobParameters();

				logger.info("sql: {}", sql);
				logger.info("queueName: {}", queueName);
				logger.info("serverUrl: {}", serverUrl);
				logger.info("userName: {}", userName);
				logger.info("jobParameters: {}", jobParameters);
				logger.info("retryCount: {}" , retryCount);

				String executionDate = jobParameters.getString("$executionDate");

				if (StringUtils.isBlank(executionDate)) {
					executionDate = new DateTime().toString("yyyy-MM-dd");
				}
				logger.info("$executionDate: {}", executionDate);
				String finalHiveSql = sql.replaceAll("\\$executionDate", executionDate);

				String var1 = jobParameters.getString("$sqlVar1");
				String var2 = jobParameters.getString("$sqlVar2");
				String var3 = jobParameters.getString("$sqlVar3");
				String var4 = jobParameters.getString("$sqlVar4");
				String var5 = jobParameters.getString("$sqlVar5");

				if (!StringUtils.isBlank(var1)) finalHiveSql = finalHiveSql.replaceAll("\\$sqlVar1", var1);
				if (!StringUtils.isBlank(var2)) finalHiveSql = finalHiveSql.replaceAll("\\$sqlVar2", var2);
				if (!StringUtils.isBlank(var3)) finalHiveSql = finalHiveSql.replaceAll("\\$sqlVar3", var3);
				if (!StringUtils.isBlank(var4)) finalHiveSql = finalHiveSql.replaceAll("\\$sqlVar4", var4);
				if (!StringUtils.isBlank(var5)) finalHiveSql = finalHiveSql.replaceAll("\\$sqlVar5", var5);

				logger.info("finalHiveSql: {}", finalHiveSql);
				chunkContext.getStepContext().getStepExecution().getExecutionContext().putString("finalHiveSql", finalHiveSql);
//				HiveConnector.excuteHiveSqlNoRes(queueName, finalHiveSql, serverUrl, userName);
				boolean exp = HiveClient.getInstance().executeHiveSqlNoRes(serverUrl,userName,queueName,finalHiveSql,null);
				if(!exp) throw new Exception(String.format("execute Hive SQL error. SQL:%s", finalHiveSql));
				logger.info("Hive SQL Execute FINISHED");
				return RepeatStatus.FINISHED;
            }
		}.retry(retryCount)).build();
	}

}
