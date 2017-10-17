package cn.rongcapital.chorus.module.test.job.launcher;

import cn.rongcapital.chorus.module.test.job.config.JobConfig;
import org.junit.runner.RunWith;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author li.hzh
 * @date 2016-11-08 14:10
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JobConfig.class})
public class SimpleJUnitTestJobLauncher {

    @Autowired
    protected JobLauncher jobLauncher;

}
