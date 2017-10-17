package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.common.exception.ServiceException;
import cn.rongcapital.chorus.common.util.DSLToXDUtil;
import cn.rongcapital.chorus.common.util.JsonUtils;
import cn.rongcapital.chorus.common.xd.XDClient;
import cn.rongcapital.chorus.common.xd.exception.ChorusXDJobDeploymentException;
import cn.rongcapital.chorus.common.xd.model.JobDeploymentProperty;
import cn.rongcapital.chorus.common.xd.model.XDJobDefinitionResource;
import cn.rongcapital.chorus.common.xd.model.XDStreamDefinitionResource;
import cn.rongcapital.chorus.das.entity.*;
import cn.rongcapital.chorus.das.service.JobDeploymentService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author li.hzh
 * @date 2016-11-22 10:46
 */
@Service
public class JobDeploymentServiceImpl implements JobDeploymentService {

    private static Logger logger = LoggerFactory.getLogger(JobDeploymentServiceImpl.class);
    private static final String JOB_DEFINITION_STATUS_DEPLOYING = "deploying";
    private static final String JOB_DEFINITION_STATUS_DEPLOYED = "deployed";
    @SuppressWarnings("unused")
    private static final String JOB_DEFINITION_STATUS_UNDEPLOYED = "undeployed";
    @SuppressWarnings("unused")
    private static final String JOB_DEFINITION_STATUS_FAILED = "failed";

    private static final int JOB_SCHEDULE_TYPE_SCHEDULE = 2;
    private static final String COMPOSED_JOB_MODULE_POSTFIX = "_COMPOSED";

    @Autowired
    private XDClient xdClient;


    @Override
    public JobDeploymentResult deployJob(Job job) {
        // 校验Job合法性
        validateJob(job);
        JobDeploymentResult jobDeploymentResult;
        if (isSingleNodeJob(job)) {
            logger.info("开始项目[{}]下的单节点任务[{}]部署。", job.getProjectName(), job.getJobName());
            jobDeploymentResult = deploySingleStepJob(job);
        } else {
            logger.info("开始项目[{}]下的多节点复合任务[{}]部署。", job.getProjectName(), job.getJobName());
            jobDeploymentResult = deployComposedJob(job);
        }
        if (JobDeploymentResult.JobDeploymentStatus.SUCCESS.equals(jobDeploymentResult.getStatus()) && isScheduleJob(job)) {
            // 创建调度任务
            deployTriggerStream(jobDeploymentResult);
        }
        return jobDeploymentResult;
    }

    @Override
    public StreamDeploymentResult deployStream(Job job) {
        validateJob(job);
        logger.debug("开始部署Stream[{}]。", job.getJobName());
        StreamDeploymentResult streamDeploymentResult = new StreamDeploymentResult(job);
        try {
            xdClient.createXDStreamDefinition(job.getJobName(), DSLToXDUtil.streamTaskDslToXd(job.getWorkFlowDSL(),
                    JSONArray.toJSONString(job.getTaskList())), false);
            List<JobDeploymentProperty> deploymentProperties = createJobDeploymentProperties(job);
            xdClient.deployStreamDefinition(job.getJobName(), job.getGroupName(), deploymentProperties);
            String status = syncGetStreamDeployStatus(job.getJobName());
            if (JOB_DEFINITION_STATUS_DEPLOYED.equals(status)) {
                logger.debug("XD Stream[{}]部署成功。DSL[{}]。", job.getJobName(), job.getWorkFlowDSL());
            } else {
                logger.error("XD Stream[{}]部署失败。状态[{}]。", job.getJobName(), status);
                throw new ChorusXDJobDeploymentException("XD Stream [" + job.getJobName() + "]部署失败。请查看日志。");
            }
            streamDeploymentResult.setStatus(StreamDeploymentResult.StreamDeploymentStatus.SUCCESS);
            // 同步获取任务部署状态
        } catch (Throwable t) {
            logger.error("部署Stream[{}]失败。DSL[{}]。", job.getJobName(), job.getWorkFlowDSL());
            logger.error("失败原因", t);
            streamDeploymentResult.setStatus(StreamDeploymentResult.StreamDeploymentStatus.FAILED);
            doRollbackStream(job.getJobName());
        }
        return streamDeploymentResult;
    }

    @Override
    public StreamDeploymentResult deployStreamCount(Job job) {
        logger.debug("开始部署StreamCount[{}]。", job.getJobName());
        StreamDeploymentResult streamDeploymentResult = new StreamDeploymentResult(job);
        try {
            xdClient.createXDStreamDefinition(job.getJobName(), job.getWorkFlowDSL(), false);
            List<JobDeploymentProperty> deploymentProperties = createJobDeploymentProperties(job);
            xdClient.deployStreamDefinition(job.getJobName(), job.getGroupName(), deploymentProperties);
            String status = syncGetStreamDeployStatus(job.getJobName());
            if (JOB_DEFINITION_STATUS_DEPLOYED.equals(status)) {
                logger.debug("XD StreamCount[{}]部署成功。DSL[{}]。", job.getJobName(), job.getWorkFlowDSL());
            } else {
                logger.error("XD StreamCount[{}]部署失败。状态[{}]。", job.getJobName(), status);
                throw new ChorusXDJobDeploymentException("XD StreamCount [" + job.getJobName() + "]部署失败。请查看日志。");
            }
            streamDeploymentResult.setStatus(StreamDeploymentResult.StreamDeploymentStatus.SUCCESS);
            // 同步获取任务部署状态
        } catch (Throwable t) {
            logger.error("部署StreamCount[{}]失败。DSL[{}]。", job.getJobName(), job.getWorkFlowDSL());
            logger.error("失败原因", t);
            streamDeploymentResult.setStatus(StreamDeploymentResult.StreamDeploymentStatus.FAILED);
            doRollbackStream(job.getJobName());
        }
        return streamDeploymentResult;
    }

    private List<JobDeploymentProperty> createJobDeploymentProperties(Job job) {
        List<Task> tasks = job.getTaskList();
        List<JobDeploymentProperty> jobDeploymentProperties = new ArrayList<>();
        for (Task task : tasks) {
            JobDeploymentProperty jobDeploymentProperty = new JobDeploymentProperty();
            jobDeploymentProperty.setModuleName(!Strings.isNullOrEmpty(task.getTaskName())
                                                        ? task.getTaskName() : task.getModuleName());
            jobDeploymentProperty.setSize(task.getInstanceSize());
            jobDeploymentProperty.setGroupName(task.getGroupName());
            jobDeploymentProperties.add(jobDeploymentProperty);
        }
        return jobDeploymentProperties;
    }

    private void deployTriggerStream(JobDeploymentResult jobDeploymentResult) {
        Job job = jobDeploymentResult.getJob();
        logger.info("创建任务[{}]定时调度trigger任务。", job.getJobName());
            /*
                该方法有可能抛出运行期异常, 异常后由于需要再次部署,
                所以依然回滚之前所有成功的节点。
              */
        String triggerJobName;
        try {
            triggerJobName = generateTriggerJobName(job);
            String triggerConfig = generateTriggerConfig(job);
            logger.debug("triggerConfig:{}", triggerJobName);
            List<JobDeploymentProperty> deploymentProperties = new ArrayList<>();;
            JobDeploymentProperty jobDeploymentProperty = new JobDeploymentProperty();
            jobDeploymentProperty.setModuleName("trigger");
            jobDeploymentProperty.setGroupName(job.getGroupName());
            jobDeploymentProperty.setSize(job.getInstanceSize());
            deploymentProperties.add(jobDeploymentProperty);
            executeStreamDeploy(triggerJobName, triggerConfig, deploymentProperties);
            logger.info("调度任务[{}]部署成功。", triggerJobName);
        } catch (Exception e) {
            logger.error("失败原因:{}", e);
            logger.info("创建Trigger任务失败,回滚任务[{}]。", job.getJobName());
            undeployJob(job);
        }
    }

    private void executeStreamDeploy(String streamName, String streamDefinition, List<JobDeploymentProperty> deploymentProperties) throws ChorusXDJobDeploymentException {
        xdClient.createXDStreamDefinition(streamName, streamDefinition, false);
        xdClient.deployStreamDefinition(streamName, "", deploymentProperties);
        // 同步获取任务部署状态
        String status = syncGetStreamDeployStatus(streamName);
        if (!JOB_DEFINITION_STATUS_DEPLOYED.equals(status)) {
            logger.error("XD Stream[{}]部署失败。任务状态[{}]。", streamName, status);
            throw new ChorusXDJobDeploymentException("XD Stream[" + streamName + "]部署失败。请查看日志。");
        }
    }

    private String syncGetStreamDeployStatus(String streamDefinitionName) {
        XDStreamDefinitionResource xdStreamDefinitionResource = null;
        while (xdStreamDefinitionResource == null ||
                       JOB_DEFINITION_STATUS_DEPLOYING.equals(xdStreamDefinitionResource.getStatus())) {
            xdStreamDefinitionResource = xdClient.getStreamDefinition(streamDefinitionName);
        }
        return xdStreamDefinitionResource.getStatus();
    }

    private String generateTriggerJobName(Job job) {
        String prefix = "trigger-";
        return isSingleNodeJob(job) ? prefix + getSingleNodeJobName(job) : prefix + job.getJobName();
    }

    private String generateTriggerConfig(Job job) {
        String triggerPattern = "trigger --cron='%s' %s > queue:job:%s";
        String payload = "";
        JSONObject variableJsonObject = new JSONObject();
        if (job.getTaskList() != null) {
            for (Task task : job.getTaskList()) {
                String variable = task.getVariable();
                if (StringUtils.isBlank(variable)) {
                    continue;
                }

                try {
                    JSONObject jsonObject = JsonUtils.toJsonObject(variable);
                    Iterator iterator = jsonObject.keySet().iterator();
                    while (iterator.hasNext()) {
                        String key = (String)iterator.next();
                        String value = jsonObject.getString(key);
                        if (StringUtils.isBlank(value))  {
                            continue;
                        }
                        if (isSingleNodeJob(job)) {
                            variableJsonObject.put(job.getJobName() + "::" + key, value);
                        } else {
                            variableJsonObject.put(task.getTaskName() + "::" + key, value);
                        }

                    }
                } catch (Exception e) {
                    logger.error("{}变量转化异常:{}", variable, e);
                    throw new ServiceException(StatusCode.DATA_DEV_VARIABLE_CONVERT_ERROR);
                }
            }
        }

        if (variableJsonObject.size() > 0) {
            try {
                payload = String.format("--payload='%s'", JsonUtils.convet2Json(variableJsonObject));
            } catch (Exception e) {
                logger.error("变量转化异常{}", e);
                throw new ServiceException(StatusCode.DATA_DEV_VARIABLE_CONVERT_ERROR);
            }

        }
//        String cronprefix = "trigger --cron='";
//        String cronpostfix = "' > queue:job:";
        Schedule schedule = job.getSchedule();
        String cronExpression = schedule.getCronExpression();

        // TODO:临时取消
//        String cronExpression = null;
//        if (!StringUtils.isBlank(schedule.getCronExpression()) && CronUtils.validateCronExpression(schedule.getCronExpression())) {
//            cronExpression = schedule.getCronExpression();
//        } else {
//            cronExpression = CronUtils.createCron(schedule.getSecond(),
//                    schedule.getMinute(), schedule.getHour(),
//                    schedule.getDay(), schedule.getMonth(),
//                    schedule.getWeek());
//        }
        String jobName = isSingleNodeJob(job) ? getSingleNodeJobName(job) : job.getJobName();
//        return cronprefix + cronExpression + cronpostfix + jobName;
        return String.format(triggerPattern, cronExpression, payload, jobName);
    }

    private String getSingleNodeJobName(Job job) {
        return job.getJobName();
    }

    private boolean isScheduleJob(Job job) {
        return job.getSchedule().getScheduleType() != null
                       && job.getSchedule().getScheduleType() == JOB_SCHEDULE_TYPE_SCHEDULE;
    }

    /**
     * 部署复合节点Job
     *
     * @param job 任务
     * @return 部署结果
     */
    private JobDeploymentResult deployComposedJob(Job job) {
        JobDeploymentResult jobDeploymentResult = new JobDeploymentResult(job);
        List<Task> taskList = job.getTaskList();
        Task currentStep = null;
        try {
            for (Task task : taskList) {
                currentStep = task;
                logger.debug("开始部署任务[{}],节点[{}],", job.getJobName(), task.getTaskName());
                doDeployComposedJobStep(job, currentStep);
                logger.info("任务[{}]节点[{}]部署成功。", job.getJobName(), currentStep.getTaskName());
                jobDeploymentResult.addSuccessStep(currentStep);
            }
        } catch (Exception e) {
            logger.error("任务[{}], 步骤[{}]部署失败。", job.getJobName(), currentStep.getTaskName());
            logger.error("失败原因。", e.getCause());
            jobDeploymentResult.setFailedStep(currentStep);
            jobDeploymentResult.setCause(e);
            jobDeploymentResult.setStatus(JobDeploymentResult.JobDeploymentStatus.STEP_FAILED);
            // 回滚部署成功的任务
            logger.info("开始回滚任务[{}]。", job.getJobName());
            rollbackJobDeployment(jobDeploymentResult);
            return jobDeploymentResult;
        }
        try {
            //部署Composed Job
            logger.debug("任务节点部署完成, 开始部署复合任务定义[{}]。", job.getJobName());
            doDeployComposedJob(job);
            logger.info("复合[{}]部署成功。", job.getJobName());
            jobDeploymentResult.setStatus(JobDeploymentResult.JobDeploymentStatus.SUCCESS);
        } catch (Exception e) {
            logger.error("部署复合任务[" + job.getJobName() + "]失败。", e);
            jobDeploymentResult.setStatus(JobDeploymentResult.JobDeploymentStatus.COMPOSED_JOB_FAILED);
            rollbackJobDeployment(jobDeploymentResult);
            doRollbackJob(job.getJobName());
        }
        return jobDeploymentResult;
    }

    private void executeDeployJob(String jobName, String jobDefinition, String groupName, Integer instanceSize, String moduleName) throws ChorusXDJobDeploymentException {
        boolean deployed = groupName == null ? true : false;
        xdClient.createXDJobDefinition(jobName, jobDefinition, deployed);
        if (groupName != null) {
            List<JobDeploymentProperty> deploymentProperties = new ArrayList<>();
            JobDeploymentProperty jobDeploymentProperty = new JobDeploymentProperty();
            jobDeploymentProperty.setModuleName(moduleName);
            jobDeploymentProperty.setSize(instanceSize == null ? 1 : instanceSize);
            deploymentProperties.add(jobDeploymentProperty);
            xdClient.deployJobDefinition(jobName, groupName, deploymentProperties);
        }
        // 同步获取任务部署状态
        String status = syncGetJobDeployStatus(jobName);
        if (JOB_DEFINITION_STATUS_DEPLOYED.equals(status)) {
            logger.debug("XD Job[{}]部署成功。DSL[{}]。", jobName, jobDefinition);
        } else {
            logger.error("XD Job[{}]部署失败。状态[{}]。DSL[{}]。", jobName, status, jobDefinition);
            throw new ChorusXDJobDeploymentException("XD Job[" + jobName + "]部署失败。请查看日志。");
        }
    }

    // 单节点job,使用job name作为definition name
    private void doDeploySingleJobStep(Job job, Task task) throws Exception {
        executeDeployJob(job.getJobName(), DSLToXDUtil.taskDslDefinitionToXd(task.getModuleName(), task.getTaskDSL()), job.getGroupName(), job.getInstanceSize(), task.getModuleName());
    }

    private void doDeployComposedJobStep(Job job, Task task) throws Exception {
        executeDeployJob(task.getTaskName(), DSLToXDUtil.taskDslDefinitionToXd(task.getModuleName(), task.getTaskDSL()), task.getGroupName(), task.getInstanceSize(), task.getModuleName());
    }

    private void doDeployComposedJob(Job job) throws Exception {
        String composedJobModuleName = generateComposedJobModuleName(job);
        executeDeployJob(job.getJobName(), DSLToXDUtil.batchWorkflowDslToXd(job.getWorkFlowDSL()), null, null, composedJobModuleName);
    }

    private String generateComposedJobModuleName(Job job) {
        return job.getJobName() + COMPOSED_JOB_MODULE_POSTFIX;
    }

    /**
     * 回滚部署成功的任务
     */
    private void rollbackJobDeployment(JobDeploymentResult jobDeploymentResult) {
        List<Task> successStepList = jobDeploymentResult.getSuccessStep();
        for (Task task : successStepList) {
            logger.debug("回滚部署成功的任务节点[{}]。", task.getTaskName());
            doRollbackJob(task.getTaskName());
        }
        Task failedStep = jobDeploymentResult.getFailedStep();
        if (failedStep != null) {
            String failedStepName = failedStep.getTaskName();
            logger.debug("回滚失败的任务节点[{}]。", failedStepName);
            doRollbackJob(failedStepName);
        }
    }

    private void doRollbackStream(String streamDefinition) {
        if (!xdClient.isStreamExist(streamDefinition)) {
            logger.warn("Stream任务[{}]不存在,无需回滚。", streamDefinition);
            return;
        }
        xdClient.destroyStream(streamDefinition);
    }

    private void doRollbackJob(String jobDefinitionName) {
        if (!xdClient.isJobExist(jobDefinitionName)) {
            logger.warn("Job任务[{}]不存在,无需回滚。", jobDefinitionName);
            return;
        }
        xdClient.destroyJobDefinition(jobDefinitionName);
    }

    /**
     * 部署单一节点Job
     *
     * @param job 任务
     * @return 部署结果
     */
    private JobDeploymentResult deploySingleStepJob(Job job) {
        Task task = job.getTaskList().get(0);
        logger.debug("开始部署任务步骤[{}]", task.getTaskName());
        JobDeploymentResult jobDeploymentResult = new JobDeploymentResult(job);
        try {
            doDeploySingleJobStep(job, task);
            logger.info("任务[{}]部署成功。", job.getJobName());
            jobDeploymentResult.setStatus(JobDeploymentResult.JobDeploymentStatus.SUCCESS);
            jobDeploymentResult.addSuccessStep(task);
        } catch (Exception e) {
            logger.error("单节点任务[" + job.getJobName() + "]部署失败。", e);
            jobDeploymentResult.setStatus(JobDeploymentResult.JobDeploymentStatus.STEP_FAILED);
            jobDeploymentResult.setFailedStep(task);
            jobDeploymentResult.setCause(e);
            doRollbackJob(job.getJobName());
        }
        return jobDeploymentResult;
    }

    /**
     * 同步获取任务状态
     */
    private String syncGetJobDeployStatus(String jobDefinitionName) {
        XDJobDefinitionResource xdJobDefinitionResource = null;
        while (xdJobDefinitionResource == null ||
                       JOB_DEFINITION_STATUS_DEPLOYING.equals(xdJobDefinitionResource.getStatus())) {
            xdJobDefinitionResource = xdClient.getJobDefinition(jobDefinitionName);
        }
        return xdJobDefinitionResource.getStatus();
    }

    @Override
    public JobDeploymentResult undeployJob(Job job) {
        logger.info("卸载任务[{}]。", job.getJobName());
        validateJob(job);
        List<Task> taskList = job.getTaskList();
        if (!isSingleNodeJob(job)) {
            for (Task task : taskList) {
                doRollbackJob(task.getTaskName());
            }
        }
        doRollbackJob(job.getJobName());
        if (isScheduleJob(job)) {
            String triggerStreamName = generateTriggerJobName(job);
            if (xdClient.isStreamExist(triggerStreamName)) {
                logger.info("卸载任务[{}]对应的调度任务。", job.getJobName());
                doRollbackStream(triggerStreamName);
            }
        }
        //TODO 任务卸载状态返回值
        return null;
    }

    @Override
    public void restartJobExecution(long jobExecutionId) {
        logger.info("重启任务jobExecutionId[{}]。", jobExecutionId);
        try {
            xdClient.restartJobExecution(jobExecutionId);
        } catch (Exception e) {
            logger.error("重启任务[" + jobExecutionId + "]失败。", e);
            throw new ServiceException(StatusCode.DATA_DEV_RESTART_ERROR);
        }
    }

    @Override
    public void undeployStream(Job job) {
        logger.info("卸载Stream任务[{}]。", job.getJobName());
        validateJob(job);
        doRollbackStream(job.getJobName());
    }

    @Override
    public void undeployStreamCount(Job job) {
        logger.info("卸载Stream Count任务[{}]。", job.getJobName());
        doRollbackStream(job.getJobName());
    }

    @Override
    public JobDeploymentResult launchJob(Job job) {
        validateJob(job);
        logger.info("手动执行任务[{}]", job.getJobName());
        xdClient.lanuchJob(job.getJobName(), job.getJobParameters());
        //TODO 查询执行状态,返回执行结果
        return null;
    }

    private void validateJob(Job job) {
        logger.debug("校验Job定义合法性。");
        if (job == null) {
            logger.error("待部署的任务为NULL, 无法继续部署。");
            throw new IllegalArgumentException("待部署的任务为NULL, 无法继续部署。");
        }
        List<Task> taskList = job.getTaskList();
        if (taskList == null || taskList.isEmpty()) {
            logger.error("待部署的任务不包含任何任务步骤信息, 无法继续部署。");
            throw new IllegalArgumentException("待部署的任务不包含任何任务步骤信息, 无法继续部署。");
        }
    }

    private boolean isSingleNodeJob(Job job) {
        return job.getTaskList().size() == 1;
    }

}
