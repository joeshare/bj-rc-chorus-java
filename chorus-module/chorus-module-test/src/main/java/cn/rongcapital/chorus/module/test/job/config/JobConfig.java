package cn.rongcapital.chorus.module.test.job.config;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author li.hzh
 * @date 2016-11-01 14:55
 */
@Configuration
@EnableBatchProcessing
@ComponentScan(basePackages = "cn.rongcapital.chorus.module")
public class JobConfig {



}
