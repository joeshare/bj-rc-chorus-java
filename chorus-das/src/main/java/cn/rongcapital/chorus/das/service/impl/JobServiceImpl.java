package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.common.dsl.xd.model.TaskDefinition;
import cn.rongcapital.chorus.common.exception.ServiceException;
import cn.rongcapital.chorus.common.util.CollectionUtils;
import cn.rongcapital.chorus.common.util.CronUtils;
import cn.rongcapital.chorus.common.util.JsonUtils;
import cn.rongcapital.chorus.common.util.StringUtils;
import cn.rongcapital.chorus.das.dao.JobMapper;
import cn.rongcapital.chorus.das.entity.ExecutingJobInfo;
import cn.rongcapital.chorus.das.entity.InstanceInfo;
import cn.rongcapital.chorus.das.entity.Job;
import cn.rongcapital.chorus.das.entity.JobDeploymentResult;
import cn.rongcapital.chorus.das.entity.PageInfo;
import cn.rongcapital.chorus.das.entity.Schedule;
import cn.rongcapital.chorus.das.entity.StreamDeploymentResult;
import cn.rongcapital.chorus.das.entity.Task;
import cn.rongcapital.chorus.das.entity.XDExecution;
import cn.rongcapital.chorus.das.entity.web.JobCause;
import cn.rongcapital.chorus.das.entity.web.TaskCause;
import cn.rongcapital.chorus.das.service.InstanceInfoService;
import cn.rongcapital.chorus.das.service.JobDeploymentService;
import cn.rongcapital.chorus.das.service.JobService;
import cn.rongcapital.chorus.das.service.ScheduleService;
import cn.rongcapital.chorus.das.service.TaskService;
import cn.rongcapital.chorus.das.xd.dao.XDMapper;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 数据任务管理SERVICE实现类
 *
 * @author lengyang
 */
@Service(value = "JobService")
@Transactional
public class JobServiceImpl implements JobService {
    private static Logger logger = LoggerFactory.getLogger(JobServiceImpl.class);
    private static final String STREAM_COUNT_SUFFIX = "_counter";
    private static final String STREAM_STATUS_DEPLOY = "DEPLOY";
    private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired(required = false)
    private JobMapper jobMapper;
    @Autowired(required = false)
    private TaskService taskService;
    @Autowired(required = false)
    private ScheduleService scheduleService;
    @Autowired(required = false)
    private JobDeploymentService jobDeploymentService;
    @Autowired(required = false)
    private InstanceInfoService instanceInfoService;
    @Autowired(required = false)
    private XDMapper xdMapper;
    
    @Value(value = "${xd.zkBasePath}")
    private String monitorSpringXdZkPath;
    @Value(value = "${zookeeper.address}")
    private String zookeeperAddress;
    @Value(value = "${zookeeper.timeout}")
    private Integer zookeeperTimeout;
    @Value(value = "${monitor.spring.xd.zk.connect.retry.time}")
    private Integer retryTime = 1000;
    @Value(value = "${monitor.spring.xd.zk.connect.retry.count}")
    private Integer retryCount = 5;
    private CuratorFramework client;

    /**
     * 添加任务
     *
     * @param job
     *            任务信息
     */
    @Override
    public void saveJob(Job job) {
        // CRON表达式验证
        if (!StringUtils.isBlank(job.getSchedule().getCronExpression())
                && !CronUtils.validateCronExpressionInterval(job.getSchedule().getCronExpression(), 10)) {
            throw new ServiceException(StatusCode.DATA_DEV_CRON_ERROR);
        }

        // job名重复定义验证
        JobCause jobCause = new JobCause();
        jobCause.setJobName(job.getJobName());
        boolean jobRresult = checkJobName(jobCause);
        if (jobRresult) {
            throw new ServiceException(4024, "XD运行任务名[" + job.getJobName() + "]" + "已存在");
        }
        // 任务步骤保存
        List<Task> newTaskList = job.getTaskList();

        // task名重复定义验证
        for (Task task : newTaskList) {
            TaskCause cause = new TaskCause();
            cause.setTaskName(task.getTaskName());
            boolean result = checkTaskName(cause);
            if (result) {
                throw new ServiceException(4023, "XD运行工作流名[" + task.getTaskName() + "]" + "已存在");
            }
        }
        // 任务保存
        Date now = new Date();
        job.setUseYn("Y");
        job.setCreateTime(now);
        job.setUpdateTime(now);
        jobMapper.addJob(job);

        for (Task task : newTaskList) {
            task.setJobId(job.getJobId());
            task.setCreateTime(now);
            task.setUpdateTime(now);
            task.setCreateUser(job.getCreateUser());
            task.setCreateUserName(job.getCreateUserName());
            task.setUpdateUser(job.getCreateUser());
            task.setUpdateUserName(job.getCreateUserName());
            // 前端生成，后端取消转换功能
            // task.setTaskDSL(DefinitionUtils.createDefinition(task.getModuleName(),
            // task.getConfig(), job
            // .getSchedule()));
            try {
                processParams(task);
            }catch(Exception e){
                logger.error("保存任务["+task.getTaskId()+":" +task.getTaskName()+"]失败", e);
                throw new ServiceException(4025,"保存任务[" +task.getTaskName()+"]失败");
            }
            taskService.saveTask(task);
        }

        // 周期任务
        if (job.getJobType() == 2) {
            Schedule schedule = job.getSchedule();
            schedule.setJobId(job.getJobId());
            schedule.setCreateTime(now);
            schedule.setUpdateTime(now);
            schedule.setCreateUser(job.getCreateUser());
            schedule.setUpdateUser(job.getCreateUser());
            scheduleService.saveSchedule(schedule);
        }
    }

    /**
     * 添加任务
     *
     * @param job
     *            任务信息
     */
    @Override
    public void updateJob(Job job) {
        // CRON表达式验证
        if (!StringUtils.isBlank(job.getSchedule().getCronExpression())
                && !CronUtils.validateCronExpressionInterval(job.getSchedule().getCronExpression(), 10)) {
            throw new ServiceException(StatusCode.DATA_DEV_CRON_ERROR);
        }

        // job名重复定义验证
        JobCause jobCause = new JobCause();
        jobCause.setJobId(job.getJobId());
        jobCause.setTaskName(job.getJobName());
        boolean jobRresult = checkJobName(jobCause);
        if (jobRresult) {
            throw new ServiceException(4024, "XD运行任务名[" + job.getJobName() + "]" + "已存在");
        }

        // 任务步骤保存
        List<Task> newTaskList = job.getTaskList();
        // task名重复定义验证
        for (Task task : newTaskList) {
            TaskCause cause = new TaskCause();
            // cause.setJobId(job.getJobId());
            cause.setTaskId(task.getTaskId());
            cause.setTaskName(task.getTaskName());
            boolean result = checkTaskName(cause);
            if (result) {
                throw new ServiceException(4023, "XD运行工作流名[" + task.getTaskName() + "]" + "已存在");
            }
        }
        // 取得任务信息
        Job dbJob = jobMapper.selectJob(job.getJobId());
        if (dbJob == null) {
            throw new ServiceException(StatusCode.DATA_DEV_NOT_EXISTS_ERROR);
        }
        // 任务保存
        Date now = new Date();
        job.setUseYn("Y");
        job.setUpdateTime(now);
        jobMapper.updateJob(job);

        // 先删除所有关联任务
        taskService.delTaskByJobId(job.getJobId());
        for (Task task : newTaskList) {
            task.setJobId(job.getJobId());
            task.setUpdateTime(now);
            task.setCreateTime(now);
            task.setCreateUser(job.getUpdateUser());
            task.setCreateUserName(job.getUpdateUserName());
            task.setUpdateUser(job.getUpdateUser());
            task.setUpdateUserName(job.getUpdateUserName());
            if (job.getJobType() == Job.JOB_TYPE_SYNC) {
                // 前台生成，后端取消转换功能
                // task.setTaskDSL(DefinitionUtils.createDefinition(task.getModuleName(),
                // task.getConfig(),
                // job.getSchedule()));
            }
            try {
                processParams(task);
            }catch(Exception e){
                logger.error("保存任务["+task.getTaskId()+":" +task.getTaskName()+"]失败", e);
                throw new ServiceException(4025,"更新任务[" +task.getTaskName()+"]失败");
            }
            taskService.saveTask(task);
        }
        // 任务调度保存
        Schedule schedule = job.getSchedule();
        schedule.setJobId(job.getJobId());
        schedule.setUpdateTime(now);
        schedule.setUpdateUser(job.getUpdateUser());
        Schedule dbSchedule = scheduleService.getScheduleByJobId(job.getJobId());
        // 周期任务进行调度信息保存
        if (job.getJobType() == 2) {
            if (dbSchedule == null) {
                schedule.setCreateUser(job.getUpdateUser());
                schedule.setCreateTime(now);
                scheduleService.saveSchedule(schedule);
            } else {
                scheduleService.updateScheduleByJobId(schedule);
            }
        }
    }

    private void processParams(Task task) throws Exception{
        TaskDefinition taskDefinition = JsonUtils.Json2Object(task.getTaskDSL(), TaskDefinition.class);
        Map<String, String> staticParams = taskDefinition.getStaticParams();
        for(Map.Entry<String, String> entry:staticParams.entrySet()){
            String value = entry.getValue().trim();
            if(entry.getKey().equals("sftpPrivateKey") || "emailContent".equals(entry.getKey())){
                staticParams.put(entry.getKey(), URLEncoder.encode(entry.getValue(),"utf-8"));
            }else if (value.indexOf(" ") > 0 && !("'".equals(value.substring(0, 1)) && "'".equals(value.substring(value.length() - 1)))) {
                staticParams.put(entry.getKey(), "'" + value + "'");
            }
        }
        taskDefinition.setStaticParams(staticParams);
        task.setTaskDSL(JSON.toJSONString(taskDefinition));
    }

    private boolean checkJobName(JobCause cause) {
        List<Job> jobList = jobMapper.validJobName(cause);
        // 判断昵称是否存在
        if (jobList != null && jobList.size() > 0) {
            // task名已存在
            return true;
        }
        return false;
    }

    private boolean checkTaskName(TaskCause cause) {
        List<Task> taskList = taskService.validTaskName(cause);
        // 判断昵称是否存在
        if (taskList != null && taskList.size() > 0) {
            // task名已存在
            return true;
        }
        return false;
    }

    /**
     * 查询所有任务
     * <p>
     * 任务
     */
    @Override
    public List<Job> selectAll() {
        List<Job> list = jobMapper.selectAll();
        return list;
    }

    @Override
    public void delJob(JobCause cause) {
        // spring xd 相关任务停止
        Job dbInfo = jobMapper.selectJob(cause.getJobId());
        if (dbInfo == null) {
            throw new ServiceException(StatusCode.DATA_DEV_NOT_EXISTS_ERROR);
        }
        if (Job.JOB_STATUS_DEPLOY.equals(dbInfo.getStatus())) {
            undeploy(cause);
        }
        // 删除任务
        cause.setUpdateTime(new Date());
        jobMapper.logicDelJob(cause);
        // 删除任务步骤
        taskService.delTaskByJobId(cause.getJobId());
        // 删除任务调度
        scheduleService.delScheduleByJobId(cause.getJobId());
    }

    @Override
    public Job selectJob(int jobId) {
        return jobMapper.selectJob(jobId);
    }

    @Override
    public Job selectExcuteJobInfos(int jobId) {
        // 取得任务信息
        Job job = jobMapper.selectJob(jobId);
        if (job == null) {
            throw new ServiceException(StatusCode.DATA_DEV_NOT_EXISTS_ERROR);
        }
        // ProjectInfo projectInfo =
        // projectInfoMapper.selectByPrimaryKey(Long.valueOf(job.getProjectId()));
        // job.setProjectName(projectInfo.getProjectName());
        // job.setProjectCode(projectInfo.getProjectCode());
        // 取得步骤信息
        List<Task> taskList = taskService.getTaskList(job.getJobId());
        job.setTaskList(taskList);

        // 取得调度信息
        Schedule schedule = scheduleService.getScheduleByJobId(job.getJobId());
        job.setSchedule(schedule);
        return job;
    }

    @Override
    public List<Job> getProjectJobList(JobCause jobCause) {
        return jobMapper.getProjectJobList(jobCause);
    }

    @Override
    public void deploy(JobCause jobCause) {
        Date now = new Date();
        Job job = selectExcuteJobInfos(jobCause.getJobId());
        if (job == null) {
            throw new ServiceException(StatusCode.DATA_DEV_NOT_EXISTS_ERROR);
        }
        // 任务保存
        job.setUpdateTime(now);
        job.setStatus(Job.JOB_STATUS_DEPLOY);
        job.setSchedule(jobCause.getSchedule());
        job.setWarningConfig(jobCause.getWarningConfig());

        // 发布信息取得
        if (jobCause.getInstanceId() != null) {
            job.setInstanceId(jobCause.getInstanceId());
        }
        InstanceInfo stepInstanceInfo = instanceInfoService.getById(jobCause.getInstanceId());
        if (stepInstanceInfo == null) {
            throw new ServiceException(StatusCode.DATA_DEV_DEPLOY_INSTANCE_NOT_EXISTS_ERROR);
        }
        String stepYarnGroupName = stepInstanceInfo.getProjectId() + "_" + stepInstanceInfo.getGroupName();
        job.setGroupName(stepYarnGroupName);
        job.setInstanceSize(stepInstanceInfo.getInstanceSize());
        if (job.getTaskList() != null) {
            for (Task item : job.getTaskList()) {
                if (StringUtils.isBlank(item.getInstanceId())) {
                    item.setInstanceId(jobCause.getInstanceId().toString());
                }
                // InstanceInfo stepInstanceInfo =
                // instanceInfoService.getById(Long.parseLong(item.getInstanceId()));
                // String stepYarnGroupName = stepInstanceInfo.getProjectId() +
                // "_" + stepInstanceInfo.getGroupName();
                item.setGroupName(stepYarnGroupName);
                item.setInstanceSize(stepInstanceInfo.getInstanceSize());
            }
        }
        if (job.getJobType() == 2) {
            JobDeploymentResult result = jobDeploymentService.deployJob(job);

            // TODO: 返回状态值非空判断（现阶段成功返回NULL）
            if (result != null) {
                switch (result.getStatus()) {
                case SUCCESS:
                    break;
                case STEP_FAILED:
                case COMPOSED_JOB_FAILED:
                    throw new ServiceException(StatusCode.DATA_DEV_DEPLOY_ERROR);
                default:
                    break;
                }
            }
        } else {
            StreamDeploymentResult result = jobDeploymentService.deployStream(job);
            if (result != null) {
                switch (result.getStatus()) {
                case SUCCESS:
                    break;
                case FAILED:
                    throw new ServiceException(StatusCode.DATA_DEV_DEPLOY_ERROR);
                default:
                }
            }

            // 生成Stream Count信息
            Job streamCountJob = getStreamCountJob(job);
            // 发布Stream Count
            StreamDeploymentResult countResult = jobDeploymentService.deployStreamCount(streamCountJob);
            if (countResult != null) {
                switch (countResult.getStatus()) {
                case SUCCESS:
                    break;
                case FAILED:
                    throw new ServiceException(StatusCode.DATA_DEV_DEPLOY_STREAM_COUNT_ERROR);
                default:
                }
            }
        }

        // 发布成功
        if (job.getJobType() == 2) {
            Schedule schedule = jobCause.getSchedule();
            schedule.setUpdateTime(now);
            schedule.setUpdateUser(job.getUpdateUser());
            Schedule dbSchedule = scheduleService.getScheduleByJobId(job.getJobId());
            if (dbSchedule == null) {
                schedule.setCreateUser(job.getUpdateUser());
                schedule.setCreateTime(now);
                scheduleService.saveSchedule(schedule);
            } else {
                scheduleService.updateScheduleByJobId(schedule);
            }
        }

        // 更新执行实例
        jobCause.setUpdateTime(now);
        jobMapper.updateJobInstance(jobCause);
        // 任务保存
        jobMapper.updateJob(job);
    }

    private Job getStreamCountJob(Job job) {
        // 部署Stream计数
        Job streamCountJob = new Job();

        String streamCountTapName = job.getJobName() + STREAM_COUNT_SUFFIX;
        streamCountJob.setJobName(streamCountTapName);

        // sample： --definition "tap:stream:springtweets > counter
        // --name=tweetcount"
        StringBuffer streamCountDsl = new StringBuffer();
        streamCountDsl.append("tap:stream:");
        streamCountDsl.append(job.getJobName());
        streamCountDsl.append(" > counter --name=");
        streamCountDsl.append(streamCountTapName);
        streamCountJob.setWorkFlowDSL(streamCountDsl.toString());
        streamCountJob.setGroupName(job.getGroupName());
        streamCountJob.setInstanceSize(job.getInstanceSize());

        // Task定义
        List<Task> countTaskList = new ArrayList<Task>();
        Task countTask = new Task();
        if (CollectionUtils.isNotEmpty(job.getTaskList())) {
            // Task jobTask = job.getTaskList().get(0);
            countTask.setTaskName(job.getJobName());
            countTask.setInstanceSize(job.getInstanceSize());
            countTask.setGroupName(job.getGroupName());
        }
        streamCountJob.setTaskList(countTaskList);
        return streamCountJob;
    }

    @Override
    public void undeploy(JobCause cause) {
        // 任务取得
        Job job = selectExcuteJobInfos(cause.getJobId());
        if (job == null) {
            throw new ServiceException(StatusCode.DATA_DEV_NOT_EXISTS_ERROR);
        }
        if (job.getJobType() == 2) {
            JobDeploymentResult result = jobDeploymentService.undeployJob(job);
            // TODO: 返回状态值非空判断（现阶段成功返回NULL）
            if (result != null) {
                switch (result.getStatus()) {
                case SUCCESS:
                    break;
                case STEP_FAILED:
                case COMPOSED_JOB_FAILED:
                    throw new ServiceException(StatusCode.DATA_DEV_UNDEPLOY_ERROR);
                default:
                }
            }
        } else {
            // Stream Count取消发布
            Job streamCountJob = new Job();
            streamCountJob.setJobName(job.getJobName() + STREAM_COUNT_SUFFIX);
            jobDeploymentService.undeployStreamCount(streamCountJob);

            // Stream取消发布
            jobDeploymentService.undeployStream(job);
        }

        // 取消发布成功
        // 任务保存
        job.setUpdateTime(new Date());
        job.setStatus(Job.JOB_STATUS_UNDEPLOY);
        jobMapper.updateJob(job);
    }

    @Override
    public void restartJobExecution(long jobExecutionId) {
        jobDeploymentService.restartJobExecution(jobExecutionId);
    }

    @Override
    public void excute(JobCause cause) {
        Job job = selectExcuteJobInfos(cause.getJobId());

        if (job == null) {
            throw new ServiceException(StatusCode.DATA_DEV_NOT_EXISTS_ERROR);
        }

        if (cause.getJobParameters() != null) {
            try {
                JSONObject finalJobParam = new JSONObject();
                Map<String, Map<String, String>> jobParametersMap = cause.getJobParameters();
                for (Map.Entry<String, Map<String, String>> entry : jobParametersMap.entrySet()) {
                    String taskName = entry.getKey();

                    for (Map.Entry<String, String> entrySub : entry.getValue().entrySet()) {
                        if (StringUtils.isBlank(entrySub.getValue()))  {
                            continue;
                        }
                        // 组合任务
                        if (job.getTaskList().size() > 1) {
                            finalJobParam.put(taskName + "::" + entrySub.getKey(), entrySub.getValue());
                        } else { // 单节点任务
                            finalJobParam.put(job.getJobName() + "::" + entrySub.getKey(), entrySub.getValue());
                        }
                    }
                }

//                String param = JsonUtils.convet2Json(cause.getJobParameters());
                String param = finalJobParam.toString();
                job.setJobParameters(param);
            } catch (Exception e) {
                throw new ServiceException(StatusCode.DATA_DEV_LAUNCH_PARAM_ERROR);
            }
        }
        jobDeploymentService.launchJob(job);
    }

    @Override
    public List<Job> checkProjectJobByAsName(JobCause jobCause) {
        return jobMapper.checkProjectJobByAsName(jobCause);
    }

    @Override
    public int count(JobCause jobCause) {
        return jobMapper.count(jobCause);
    }

    @Override
    public Job selectJobByName(String jobName) {
        return jobMapper.selectJobByName(jobName);
    }

    @Override
    public Map<String, Long> getJobStatusDistribution(long projectId) {
        return jobMapper.getJobStatusDistribution(projectId);
    }

    @Override
    public List<Job> getAllStreamJob() {
        return jobMapper.getAllJobByType(1, true);
    }

    @Override
    public List<Job> getAllBatchJob() {
        return jobMapper.getAllJobByType(2, false);
    }

    @Override
    public List<Job> getJobs(List<XDExecution> xdExecutions) {
        return jobMapper.getJobs(xdExecutions);
    }
    
    @Override
    public PageInfo<ExecutingJobInfo> getExecutingBatchJobList(Integer pageNum, Integer size) {
        PageInfo<ExecutingJobInfo> pageInfo = new PageInfo<>();
        pageInfo.setData(new ArrayList<ExecutingJobInfo>());
        pageInfo.setTotalNum(0);
        
        List<XDExecution> execList = xdMapper.getAllChorusExecutingXDExecutions();
        if(CollectionUtils.isEmpty(execList)) {
            return pageInfo;
        }
        
        Set<String> jobNames = execList.stream().map(XDExecution::getJobName).collect(Collectors.toSet());
        List<Job> execJobs = jobMapper.getExecutingJobs(Job.JOB_TYPE_SYNC, jobNames);
        if(CollectionUtils.isEmpty(execJobs)) {
            return pageInfo;
        }
        
        List<ExecutingJobInfo> ejobs = mergeToExecutingJobList(execList, execJobs);
        if(CollectionUtils.isEmpty(ejobs)) {
            return pageInfo;
        }
        
        ejobs = ejobs.stream().sorted(Comparator.comparing(ExecutingJobInfo::getProjectName)).collect(Collectors.toList());
        pageInfo.setTotalNum(ejobs.size());
        if(pageNum!=null && size!=null) {
            pageInfo.setData(ejobs.stream().skip((pageNum-1)*size).limit(size).collect(Collectors.toList()));
        } else {
            pageInfo.setData(ejobs);
        }
        
        return pageInfo;
    }

    @Override
    public List<ExecutingJobInfo> getExecutingBatchJobByInstanceId(Long instanceId) {
        List<Job> deployedJobs = jobMapper.getDeployedByInstanceIdAndType(instanceId, Job.JOB_TYPE_SYNC);
        if (CollectionUtils.isNotEmpty(deployedJobs)){
            List<String> jobNames = deployedJobs.stream().map(job -> job.getJobName()).collect(Collectors.toList());
            List<XDExecution> executingXDExecutions = xdMapper.getExecutingXDExecutions(jobNames);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (CollectionUtils.isNotEmpty(executingXDExecutions)){
               return executingXDExecutions.stream().map(e -> {
                    ExecutingJobInfo executingJobInfo = new ExecutingJobInfo();
                    executingJobInfo.setJobName(e.getJobName());
                    executingJobInfo.setJobType(Job.JOB_TYPE_SYNC);
                    executingJobInfo.setJobInstanceId(e.getInstanceId());
                    executingJobInfo.setJobExecutionId(e.getExecutionId());
                    executingJobInfo.setJobStartTime(df.format(e.getStartTime()));
                    return executingJobInfo;
               }).collect(Collectors.toList());
            } else {
                return Collections.emptyList();
            }
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public List<ExecutingJobInfo> getExecutingStreamByInstanceId(Long instanceId) {
        List<Job> deployedStream = jobMapper.getDeployedByInstanceIdAndType(instanceId, Job.JOB_TYPE_REAL);
        if (CollectionUtils.isNotEmpty(deployedStream)){
            return deployedStream.stream()
                    .filter(s -> {
                        String status = getStreamJobStatus(s.getJobName());
                        if (StringUtils.isNotBlank(status)){
                           return status.toUpperCase().startsWith(STREAM_STATUS_DEPLOY);
                        } else {
                            return false;
                        }
                    })
                    .map(s -> {
                        ExecutingJobInfo executingJobInfo = new ExecutingJobInfo();
                        executingJobInfo.setJobName(s.getJobName());
                        executingJobInfo.setJobType(Job.JOB_TYPE_REAL);
                        return executingJobInfo;
                    })
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * 合并XDExecution和Job数据
     * @param execList xd正在执行的批量任务信息
     * @param execJobs xd正在执行的任务对应的job信息
     * @return 合并后的执行信息
     */
    private List<ExecutingJobInfo> mergeToExecutingJobList(List<XDExecution> execList, List<Job> execJobs){
        List<ExecutingJobInfo> ejList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(execList) && CollectionUtils.isNotEmpty(execJobs)) {
            Map<String, Job> jobMap = new HashMap<>();
            for (Job execJob : execJobs) {
                jobMap.put(execJob.getJobName(), execJob);
            }
            
            for (XDExecution xdExecution : execList) {
                String jobName = xdExecution.getJobName();
                if(!jobMap.containsKey(jobName)) {
                    continue;
                }
                
                Job job = jobMap.get(jobName);
                ExecutingJobInfo eji = new ExecutingJobInfo();
                eji.setCreateUserName(job.getCreateUserName());
                eji.setDescription(job.getDescription());
                eji.setJobExecutionId(xdExecution.getExecutionId());
                eji.setJobInstanceId(xdExecution.getInstanceId());
                eji.setJobName(job.getJobAliasName());
                eji.setJobStartTime(df.format(xdExecution.getStartTime()));
                eji.setJobType(Job.JOB_TYPE_SYNC);
                eji.setProjectName(job.getProjectName());
                ejList.add(eji);
            }
        }
        
        return ejList;
    }
    
    @Override
    public PageInfo<ExecutingJobInfo> getExecutingStreamJobList(Integer pageNum, Integer size) {
        PageInfo<ExecutingJobInfo> pageInfo = new PageInfo<>();
        
        List<Job> executingJobs = jobMapper.getExecutingJobs(Job.JOB_TYPE_REAL, null);
        if(CollectionUtils.isNotEmpty(executingJobs)) {
            Map<String, String> jobNameMap = new HashMap<>();
            List<ExecutingJobInfo> execJobs = convertToExecutingJobList(executingJobs, jobNameMap);
            Iterator<ExecutingJobInfo> ejsIt = execJobs.iterator();
            while(ejsIt.hasNext()) {
                ExecutingJobInfo execJob = ejsIt.next();
                String jobName = execJob.getJobName();
                String status = getStreamJobStatus(jobNameMap.get(jobName));
                if(!StringUtils.isEmpty(status) && !status.startsWith(STREAM_STATUS_DEPLOY) && !status.startsWith(STREAM_STATUS_DEPLOY.toLowerCase())){
                    ejsIt.remove();
                }
            }
            
            pageInfo.setTotalNum(execJobs.size());
            if(pageNum!=null && size!=null) {
                pageInfo.setData(execJobs.stream().skip((pageNum-1)*size).limit(size).collect(Collectors.toList()));
            } else {
                pageInfo.setData(execJobs);
            }
        } else {
            pageInfo.setData(new ArrayList<ExecutingJobInfo>());
            pageInfo.setTotalNum(0);
        }
        
        return pageInfo;
    }
    
    /**
     * 将Job对象转为ExecutingJobInfo对象
     */
    private List<ExecutingJobInfo> convertToExecutingJobList(List<Job> jobs, Map<String, String> map) {
        List<ExecutingJobInfo> ejList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(jobs)) {
            for (Job job : jobs) {
                ExecutingJobInfo eji = new ExecutingJobInfo();
                eji.setJobName(job.getJobAliasName());
                eji.setDescription(job.getDescription());
                eji.setJobStartTime(df.format(job.getUpdateTime()));
                eji.setJobType(Job.JOB_TYPE_REAL);
                eji.setCreateUserName(job.getCreateUserName());
                eji.setProjectName(job.getProjectName());
                ejList.add(eji);
                
                map.put(job.getJobAliasName(), job.getJobName());
            }
        }
        return ejList;
    }
    
    /**
     * 到zookeeper上抓取stream的状态
     * @param jobName 任务名
     * @return 任务状态
     */
    private String getStreamJobStatus(String jobName){
        String status = null;
        String nodePath = getStreamStatusNode(jobName);
        try {
            if(getClient().checkExists().forPath(nodePath) != null){
                String nodeValue = new String(getClient().getData().forPath(nodePath));
                if(StringUtils.isNotEmpty(nodeValue)) {
                    JSONObject object = JSONObject.parseObject(nodeValue);
                    status = object.getString("state");
                }
            } 
        } catch (Exception e) {
            logger.error("JOB STATISTIC - GET ZOOKEEPER NODE EXCEPTION! nodePath = {}", nodePath, e);
        } 
        return status;
    }
    
    /**
     * 获取zookeeper上任务状态节点路径
     * @param jobName 任务名
     * @return 节点路径
     */
    private String getStreamStatusNode(String jobName){
        StringBuilder nodePath = new StringBuilder(monitorSpringXdZkPath);
        nodePath.append("/deployments/streams/");
        nodePath.append(jobName);
        nodePath.append("/status");
        return nodePath.toString();
    }
    
    private CuratorFramework getClient(){
        if(client==null){
            RetryPolicy retryPolicy = new ExponentialBackoffRetry(retryTime, retryCount);
            client = CuratorFrameworkFactory.builder().connectString(zookeeperAddress)
              .sessionTimeoutMs(zookeeperTimeout).retryPolicy(retryPolicy).build();

            client.start();
        }
        return client;
    }

    @Override
    public boolean isRunning(Integer jobId) {
        Job job = jobMapper.selectJob(jobId);
        if (job == null) {
            throw new ServiceException(StatusCode.DATA_DEV_NOT_EXISTS_ERROR);
        }
        return org.apache.commons.lang3.StringUtils.equals(Job.JOB_STATUS_DEPLOY, job.getStatus());
    }
}
