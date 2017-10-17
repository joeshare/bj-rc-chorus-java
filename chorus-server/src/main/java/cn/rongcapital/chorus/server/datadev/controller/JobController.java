package cn.rongcapital.chorus.server.datadev.controller;

import cn.rongcapital.chorus.common.constant.JobType;
import cn.rongcapital.chorus.common.constant.ScheduleType;
import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.common.dsl.xd.model.TaskDefinition;
import cn.rongcapital.chorus.common.dsl.xd.model.WorkFlowForXDGTree;
import cn.rongcapital.chorus.common.exception.ServiceException;
import cn.rongcapital.chorus.common.util.*;
import cn.rongcapital.chorus.das.entity.Job;
import cn.rongcapital.chorus.das.entity.Schedule;
import cn.rongcapital.chorus.das.entity.Task;
import cn.rongcapital.chorus.das.entity.web.JobCause;
import cn.rongcapital.chorus.das.entity.web.ScheduleCause;
import cn.rongcapital.chorus.das.entity.web.TaskCause;
import cn.rongcapital.chorus.das.service.InstanceInfoService;
import cn.rongcapital.chorus.das.service.JobService;
import cn.rongcapital.chorus.das.service.ScheduleService;
import cn.rongcapital.chorus.das.service.TaskService;
import cn.rongcapital.chorus.server.datadev.controller.vo.*;
import cn.rongcapital.chorus.server.vo.ResultVO;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class JobController {
    private static Logger logger = LoggerFactory.getLogger(JobController.class);
    
    /**
     * 数据任务
     */
    @Autowired
    private JobService jobService = null;
    
    /**
     * 任务步骤
     */
    @Autowired
    private TaskService taskService = null;
    
    /**
     * 任务调度
     */
    @Autowired
    private ScheduleService scheduleService = null;
    
    /**
     * Spring XD实例
     */
    @Autowired
    private InstanceInfoService instanceInfoService = null;
    
    /**
     * 验证CRON
     *
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = {"/job/validCronWithInterval"}, method = RequestMethod.POST)
    @ResponseBody
    public ResultVO validCronWithInterval(@RequestBody ScheduleCause cause) {
        ResultVO resultVO = ResultVO.error();
        
        boolean result = CronUtils.validateCronExpression(cause.getCronExpression());
        if (result) {
            result = CronUtils.validateCronExpressionInterval(cause.getCronExpression(), 10);
            if (!result) {
                resultVO.setMsg("请输入间隔10分钟以上的Cron表达式");
            } else {
                resultVO = new ResultVO(StatusCode.SUCCESS, "验证成功");
            }
        } else {
            resultVO.setMsg("Cron表达式错误");
        }
        return resultVO;
    }
    
    /**
     * 验证Job昵称
     */
    @RequestMapping(value = {"/job/validJobAliasName"}, method = RequestMethod.POST)
    @ResponseBody
    public ResultVO validJobAliasName(@RequestBody JobCause cause) {
        try {
            if (cause.getProjectId() == null) {
                return new ResultVO(StatusCode.DATA_DEV_PROJECT_ID_NULL_ERROR);
            }
            if (StringUtils.isBlank(cause.getJobAliasName())) {
                return new ResultVO(StatusCode.DATA_DEV_JOB_AS_NAME_NULL_ERROR);
            }
            // 任务类型验证
            JobType.getJobTypeByCode(cause.getJobType());
            
            List<Job> jobList = jobService.checkProjectJobByAsName(cause);
            
            // 判断昵称是否存在
            if (jobList != null && jobList.size() > 0) {
                // job名已存在
                return new ResultVO(StatusCode.DATA_DEV_JOB_AS_NAME_EXISTS_ERROR);
            }
        } catch (ServiceException se) {
            logger.error("valid jobAliasName error", se);
            return ResultVO.error(se);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResultVO.error();
        }
        
        return ResultVO.success();
    }
    
    /**
     * 验证TaskName是否存在
     */
    @RequestMapping(value = {"/job/validTaskName"}, method = RequestMethod.POST)
    @ResponseBody
    public ResultVO validTaskName(@RequestBody TaskCause cause) {
        try {
            if (StringUtils.isBlank(cause.getTaskName())) {
                return new ResultVO(StatusCode.DATA_DEV_TASK_NAME_NULL_ERROR);
            }
            List<Task> taskList = taskService.validTaskName(cause);
            
            // 判断昵称是否存在
            if (taskList != null && taskList.size() > 0) {
                // task名已存在
                return new ResultVO(StatusCode.DATA_DEV_TASK_NAME_EXISTS_ERROR);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResultVO.error();
        }
        
        return ResultVO.success();
    }
    
    /**
     * 节点列表
     *
     * @param pageNum  页数
     * @param pageSize 每页数据量
     * @return
     */
    @RequestMapping(value = "/job/projectJob/{pageNum}/{pageSize}", method = RequestMethod.POST)
    @ResponseBody
    public ResultVO<PageInfo> listJob(@PathVariable int pageNum, @PathVariable int pageSize, @RequestBody JobCause jobCause) {
        jobCause.setPage(pageNum);
        jobCause.setRowCnt(pageSize);
        
        if (jobCause.getProjectId() == null) {
            return new ResultVO(StatusCode.DATA_DEV_PROJECT_ID_NULL_ERROR);
        }
        if (jobCause.getJobType() != 3) {
            // 任务类型验证
            JobType.getJobTypeByCode(jobCause.getJobType());
        }
        
        List<Job> jobList = jobService.getProjectJobList(jobCause);
        
        // list声明
        List<JobInfosVO> list = new ArrayList<JobInfosVO>();
        
        // 如果查询数据存在
        if (jobList != null) {
            // 获取用户数据
            for (Job item : jobList) {
                JobInfosVO jobInfosVO = OrikaBeanMapper.INSTANCE.map(item, JobInfosVO.class);
                jobInfosVO.setFormatTime(item.getCreateTime());
                list.add(jobInfosVO);
            }
        }
        int count = jobService.count(jobCause);
        PageInfo page = new PageInfo();
        page.setPageNum(pageNum);
        page.setPageSize(pageSize);
        page.setTotal(count);
        page.setList(list);
        return ResultVO.success(page);
    }
    
    /**
     * 部署信息取得
     *
     * @param cause
     */
    @RequestMapping(value = {"/job/deployInfo"}, method = RequestMethod.POST)
    @ResponseBody
    public ResultVO<DeployInfoVO> deployInfo(@RequestBody JobCause cause) {
        DeployInfoVO deployInfoVO = new DeployInfoVO();
        try {
            if (cause.getJobId() == null) {
                return new ResultVO(StatusCode.DATA_DEV_JOB_ID_NULL_ERROR);
            }
            Job job = jobService.selectJob(cause.getJobId());
            
            // 判断任务是否存在
            if (job == null) {
                // 任务不存在
                return new ResultVO(StatusCode.DATA_DEV_NOT_EXISTS_ERROR);
            }
            // instanceID
            deployInfoVO.setInstanceId(job.getInstanceId());
            // 报警配置
            deployInfoVO.setWarningConfig(job.getWarningConfig());
            
            // 调度信息取得
            Schedule schedule = scheduleService.getScheduleByJobId(job.getJobId());
            ScheduleVO scheduleVo = new ScheduleVO();
            scheduleVo.setScheduleId(schedule.getScheduleId());
            
            scheduleVo.setScheduleType(ScheduleType.getScheduleTypeByCode(schedule.getScheduleType()));
            scheduleVo.setCronExpression(schedule.getCronExpression());
            deployInfoVO.setSchedule(scheduleVo);
        } catch (ServiceException se) {
            logger.error("get deploy info error", se);
            return ResultVO.error(se);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResultVO.error();
        }
        
        return ResultVO.success(deployInfoVO);
    }
    
    /**
     * 删除任务信息
     *
     * @param cause
     */
    @RequestMapping(value = {"/job/delJob"}, method = RequestMethod.POST)
    @ResponseBody
    public ResultVO delJob(@RequestBody JobCause cause) {
        try {
            if (cause.getJobId() == null) {
                return new ResultVO(StatusCode.DATA_DEV_JOB_ID_NULL_ERROR);
            }
            if (StringUtils.isBlank(cause.getUpdateUser())) {
                return new ResultVO(StatusCode.DATA_DEV_UPDATE_USER_ID_NULL_ERROR);
            }
            if (StringUtils.isBlank(cause.getUpdateUserName())) {
                return new ResultVO(StatusCode.DATA_DEV_UPDATE_USER_NAME_NULL_ERROR);
            }
            jobService.delJob(cause);
        } catch (ServiceException se) {
            logger.error("del job error", se);
            return ResultVO.error(se);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResultVO.error();
        }
        return ResultVO.success();
    }
    
    /**
     * 取消发布
     *
     * @param cause
     * @return
     */
    @RequestMapping(value = "/job/undeploy", method = RequestMethod.POST)
    @ResponseBody
    public ResultVO undeploy(@RequestBody JobCause cause) {
        try {
            if (cause.getJobId() == null) {
                return new ResultVO(StatusCode.DATA_DEV_JOB_ID_NULL_ERROR);
            }
            if (StringUtils.isBlank(cause.getDeployUserId())) {
                return new ResultVO(StatusCode.DATA_DEV_DEPLOY_USER_ID_NULL_ERROR);
            }
            if (StringUtils.isBlank(cause.getDeployUserName())) {
                return new ResultVO(StatusCode.DATA_DEV_DEPLOY_USER_NAME_NULL_ERROR);
            }
            cause.setUpdateUser(cause.getDeployUserId());
            cause.setUpdateUserName(cause.getDeployUserName());
            jobService.undeploy(cause);
        } catch (ServiceException se) {
            logger.error("undeploy job error", se);
            return ResultVO.error(se);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResultVO.error();
        }
        return ResultVO.success();
    }
    
    /**
     * 发布
     *
     * @param cause
     * @return
     */
    @RequestMapping(value = "/job/deploy", method = RequestMethod.POST)
    @ResponseBody
    public ResultVO deploy(@RequestBody JobCause cause) {
        try {
            if (cause.getJobId() == null) {
                return new ResultVO(StatusCode.DATA_DEV_JOB_ID_NULL_ERROR);
            }
            if (StringUtils.isBlank(cause.getDeployUserId())) {
                return new ResultVO(StatusCode.DATA_DEV_DEPLOY_USER_ID_NULL_ERROR);
            }
            if (StringUtils.isBlank(cause.getDeployUserName())) {
                return new ResultVO(StatusCode.DATA_DEV_DEPLOY_USER_NAME_NULL_ERROR);
            }
            // 执行容器ID验证
            if (cause.getInstanceId() == null) {
                return new ResultVO(StatusCode.DATA_DEV_DEPLOY_INSTANCE_ID_NULL_ERROR);
            }
            cause.setUpdateUser(cause.getDeployUserId());
            cause.setUpdateUserName(cause.getDeployUserName());
            jobService.deploy(cause);
        } catch (ServiceException se) {
            logger.error("deploy job error", se);
            return ResultVO.error(se);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResultVO.error();
        }
        return ResultVO.success();
    }
    
    /**
     * 任务信息查询
     *
     * @param cause
     */
    @RequestMapping(value = {"/job/getJobInfo"}, method = RequestMethod.POST)
    @ResponseBody
    public ResultVO getJobInfo(@RequestBody JobCause cause) {
        JobInfoVO jobInfoVO = new JobInfoVO();
        try {
            if (cause.getJobId() == null) {
                return new ResultVO(StatusCode.DATA_DEV_JOB_ID_NULL_ERROR);
            }
            Job job = jobService.selectExcuteJobInfos(cause.getJobId());
            jobInfoVO = OrikaBeanMapper.INSTANCE.map(job, JobInfoVO.class);
            jobInfoVO.setFormatTime(job.getCreateTime());
            jobInfoVO.setJobType(JobType.getJobTypeByCode(job.getJobType()));
            jobInfoVO.setDataInput(job.getDataInput());
            jobInfoVO.setDataOutput(job.getDataOutput());
            Schedule schedule = job.getSchedule();
            if (schedule != null) {
                ScheduleVO scheduleVo = new ScheduleVO();
                
                if (JobType.CYCLE.equals(JobType.getJobTypeByCode(job.getJobType())) && schedule.getScheduleId() != null) {
                    scheduleVo.setScheduleId(schedule.getScheduleId());
                    
                    scheduleVo.setScheduleType(ScheduleType.getScheduleTypeByCode(schedule.getScheduleType()));
                    scheduleVo.setCronExpression(schedule.getCronExpression());
                }
                jobInfoVO.setSchedule(scheduleVo);
            }
            
            List<TaskVO> taskVOList = new ArrayList<TaskVO>();
            List<Task> taskList = job.getTaskList();
            for (Task task : taskList) {
                TaskVO taskVO = new TaskVO();
                taskVO.setTaskId(task.getTaskId());
                taskVO.setTaskName(task.getTaskName());
                taskVO.setModuleName(task.getModuleName());
                taskVO.setModuleType(task.getModuleType());
                taskVO.setTaskDSL(processTaskList(task.getTaskDSL()));
                taskVO.setVariable(task.getVariable());
                taskVOList.add(taskVO);
            }
            jobInfoVO.setTaskList(taskVOList);
            
        } catch (ServiceException se) {
            logger.error("get job info error", se);
            return ResultVO.error(se);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResultVO.error();
        }
        return ResultVO.success(jobInfoVO);
    }
    
    /**
     * 添加
     *
     * @param job
     * @return
     */
    @RequestMapping(value = "/job/save", method = RequestMethod.POST)
    @ResponseBody
    public ResultVO save(@RequestBody Job job) {
        try {
            // 项目ID验证
            if (job.getProjectId() == null) {
                return new ResultVO(StatusCode.DATA_DEV_PROJECT_ID_NULL_ERROR);
            }
            // 任务类型验证
            JobType.getJobTypeByCode(job.getJobType());
            
            // 工作流定义验证
            if (StringUtils.isBlank(job.getWorkFlowDSL())) {
                return new ResultVO(StatusCode.DATA_DEV_JOB_DSL_NULL_ERROR);
            }
            // 工作流节点定义验证
            if (CollectionUtils.isEmpty(job.getTaskList())) {
                return new ResultVO(StatusCode.DATA_DEV_TASK_LIST_NULL_ERROR);
            }
            // 执行容器ID验证
//            if (job.getInstanceId() == null) {
//                return new ResultVO(StatusCode.DATA_DEV_DEPLOY_INSTANCE_ID_NULL_ERROR);
//            }
            // 调度方式验证
            if (JobType.CYCLE.equals(JobType.getJobTypeByCode(job.getJobType()))) {
                if (job.getSchedule() == null || job.getSchedule().getScheduleType() == null) {
                    return new ResultVO(StatusCode.SCHEDULE_TYPE_NULL_ERROR);
                } else {
                    // 类型验证
                    ScheduleType.getScheduleTypeByCode(job.getSchedule().getScheduleType());
                }
            }
            
            WorkFlowForXDGTree wfXDTree = new WorkFlowForXDGTree(job.getWorkFlowDSL());
            String jobName = wfXDTree.getWorkFlow().getName();
            if (StringUtils.isBlank(jobName)) {
                return new ResultVO(StatusCode.DATA_DEV_JOB_NAME_NULL_ERROR);
            }
            job.setJobName(jobName);
            
            Integer jobId = job.getJobId();
            if (jobId == null) {
                // 新建
                if (StringUtils.isBlank(job.getCreateUser())) {
                    return new ResultVO(StatusCode.DATA_DEV_CREATE_USER_ID_NULL_ERROR);
                }
                if (StringUtils.isBlank(job.getCreateUserName())) {
                    return new ResultVO(StatusCode.DATA_DEV_CREATE_USER_NAME_NULL_ERROR);
                }
                jobService.saveJob(job);
            } else {
                if(jobService.isRunning(job.getJobId())) return new ResultVO(StatusCode.DATA_DEV_RUNNING_JOB_UPDATE_FORBID);
                // 更新
                if (StringUtils.isBlank(job.getUpdateUser())) {
                    return new ResultVO(StatusCode.DATA_DEV_UPDATE_USER_ID_NULL_ERROR);
                }
                if (StringUtils.isBlank(job.getUpdateUserName())) {
                    return new ResultVO(StatusCode.DATA_DEV_UPDATE_USER_NAME_NULL_ERROR);
                }
                jobService.updateJob(job);
            }
        } catch (ServiceException se) {
            logger.error("save job info error", se);
            return ResultVO.error(se);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResultVO.error();
        }
        JSONObject result = new JSONObject();
        result.put("jobId", job.getJobId());
        return new ResultVO(StatusCode.SUCCESS, result);
    }
    
    /**
     * 立即执行
     *
     * @param cause
     * @return
     */
    @RequestMapping(value = "/job/excute", method = RequestMethod.POST)
    @ResponseBody
    public ResultVO excute(@RequestBody JobCause cause) {
        try {
            if (cause.getJobId() == null) {
                return new ResultVO(StatusCode.DATA_DEV_JOB_ID_NULL_ERROR);
            }
            jobService.excute(cause);
        } catch (ServiceException se) {
            logger.error("excute job error", se);
            return ResultVO.error(se);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResultVO.error();
        }
        return ResultVO.success();
    }
    
    /**
     * 重启
     *
     * @param jobExecutionId
     * @return
     */
    @RequestMapping(value = "/job/restartJobExecution/{jobExecutionId}", method = RequestMethod.POST)
    @ResponseBody
    public ResultVO restartJobExecution(@PathVariable Long jobExecutionId) {
        try {
            jobService.restartJobExecution(jobExecutionId);
        } catch (ServiceException se) {
            logger.error("restart job execution error", se);
            return ResultVO.error(se);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResultVO.error();
        }
        return ResultVO.success();
    }

    private String processTaskList(String taskDSL) throws Exception {

        TaskDefinition taskDefinition = JsonUtils.Json2Object(taskDSL, TaskDefinition.class);
        Map<String, String> staticParams = taskDefinition.getStaticParams();
        for(Map.Entry<String, String> entry:staticParams.entrySet()){
            String value = entry.getValue();
            if(entry.getKey().equals("sftpPrivateKey") || "emailContent".equals(entry.getKey())){
                staticParams.put(entry.getKey(), URLDecoder.decode(entry.getValue(), "utf-8"));
            }
            if(value.indexOf(" ")>0 && "'".equals(value.substring(0, 1)) && "'".equals(value.substring(value.length() - 1))){
                staticParams.put(entry.getKey(),value.substring(1, value.length() - 1));
            }
        }
        taskDefinition.setStaticParams(staticParams);
        return JSON.toJSONString(taskDefinition);
    }
}
