package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.common.exception.ServiceException;
import cn.rongcapital.chorus.das.dao.JobMapper;
import cn.rongcapital.chorus.das.entity.ExecutingJobInfo;
import cn.rongcapital.chorus.das.entity.Job;
import cn.rongcapital.chorus.das.entity.Schedule;
import cn.rongcapital.chorus.das.entity.Task;
import cn.rongcapital.chorus.das.entity.XDExecution;
import cn.rongcapital.chorus.das.entity.web.JobCause;
import cn.rongcapital.chorus.das.service.ScheduleService;
import cn.rongcapital.chorus.das.service.TaskService;
import cn.rongcapital.chorus.das.service.impl.JobServiceImpl;
import cn.rongcapital.chorus.das.xd.dao.XDMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyList;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
public class JobServiceImplTest {
    @InjectMocks
    JobServiceImpl jobService;
    @Mock
    private XDMapper xdMapper;
    @Mock
    private JobMapper jobMapper;
    @Mock
    private TaskService taskService;
    @Mock
    private ScheduleService scheduleService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
    @Test
    public void getExecutingBatchJobByInstanceId(){

        long instanceId = 1;
        List<Job> deployedJobs = new ArrayList<>();
        Job job1 = new Job();
        job1.setJobName("job1");
        Job job2 = new Job();
        job2.setJobName("job2");
        deployedJobs.add(job1);
        deployedJobs.add(job2);

        when(jobMapper.getDeployedByInstanceIdAndType(instanceId,Job.JOB_TYPE_SYNC)).thenReturn(deployedJobs);
        List<XDExecution> executingBatchJobs = new ArrayList<>();
        XDExecution execution1 = new XDExecution();
        execution1.setExecutionId(1);
        execution1.setStartTime(new Date());
        XDExecution execution2 = new XDExecution();
        execution2.setExecutionId(2);
        execution2.setStartTime(new Date());
        executingBatchJobs.add(execution1);
        executingBatchJobs.add(execution2);
        when(xdMapper.getExecutingXDExecutions(anyList())).thenReturn(executingBatchJobs);

        List<ExecutingJobInfo> executingJobInfos = jobService.getExecutingBatchJobByInstanceId(instanceId);
        assertEquals(2, executingJobInfos.size());
        assertEquals(1,executingJobInfos.get(0).getJobExecutionId().intValue());

    }

    /**
     * 
     * @author yunzhong
     * @time 2017年9月11日上午10:30:46
     */
    @Test
    public void testUndeployWithExecutions() {
        List<XDExecution> values = new ArrayList<>();
        XDExecution value = new XDExecution();
        values.add(value);
        when(xdMapper.getJobExecutions("jobName", Arrays.asList("STARTING", "STARTED", "STOPPING"))).thenReturn(values);
        Job job = new Job();
        job.setJobId(10000);
        job.setJobName("jobName");
        when(jobMapper.selectJob(10000)).thenReturn(job);
        when(taskService.getTaskList(10000)).thenReturn(new ArrayList<>());
        when(scheduleService.getScheduleByJobId(10000)).thenReturn(new Schedule());
        JobCause cause = new JobCause();
        cause.setJobId(10000);
        try {
            jobService.undeploy(cause);
        } catch (ServiceException e) {
            if (StatusCode.DATA_DEV_EXECUTING_ERROR.getCode().equals(e.getCode())) {
                return;
            } else {
                fail(e.getLocalizedMessage());
            }

        }
        fail("not stop");
    }
}
