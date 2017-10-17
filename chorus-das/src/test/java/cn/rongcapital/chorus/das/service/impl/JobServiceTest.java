package cn.rongcapital.chorus.das.service.impl;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import cn.rongcapital.chorus.das.entity.Job;
import cn.rongcapital.chorus.das.entity.XDExecution;
import cn.rongcapital.chorus.das.service.JobService;
import cn.rongcapital.chorus.das.util.BasicSpringTest;

public class JobServiceTest extends BasicSpringTest {
    
    @Autowired
    private JobService jobService;
    

    @Test
    public void test(){
        List<XDExecution> xdExecutions = new ArrayList<>();
        XDExecution exExe  = new XDExecution();
        exExe.setExecutionId(1);
        exExe.setJobName("chorus_twbBqqaKPazakvrxnHQV1498467184097");
        xdExecutions.add(exExe );
        
        XDExecution exExe2  = new XDExecution();
        exExe2.setExecutionId(2);
        exExe2.setJobName("chorus_xXnsEWmNDKQtdwEYglxu1498207970109");
        xdExecutions.add(exExe2 );
        
        List<Job> jobs = jobService.getJobs(xdExecutions );
        assertNotNull(jobs);
        assertTrue(jobs.size() == 2);
        for(Job job:jobs){
            assertNotNull(job.getCreateUserName());
        }
    }
}
