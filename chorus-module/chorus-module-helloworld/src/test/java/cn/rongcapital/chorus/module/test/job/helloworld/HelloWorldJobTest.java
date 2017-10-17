package cn.rongcapital.chorus.module.test.job.helloworld;

import cn.rongcapital.chorus.module.test.job.launcher.SimpleJUnitTestJobLauncher;
import org.junit.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author li.hzh
 * @date 2016-11-08 15:30
 */
public class HelloWorldJobTest extends SimpleJUnitTestJobLauncher {

    @Autowired
    private Job helloWorldJob;

    @Autowired
    private Job job;

    @Test
    public void testJobWithAnnotation() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        jobLauncher.run(helloWorldJob, new JobParameters());
    }

    @Test
    public void testJobWithXml() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        jobLauncher.run(job, new JobParameters());
    }

}
