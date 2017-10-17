package cn.rongcapital.chorus.server.job.monitor.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;

import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.common.exception.ServiceException;
import cn.rongcapital.chorus.common.util.OrikaBeanMapper;
import cn.rongcapital.chorus.common.util.StringUtils;
import cn.rongcapital.chorus.das.bo.LogBO;
import cn.rongcapital.chorus.das.entity.Job;
import cn.rongcapital.chorus.das.entity.JobMonitor;
import cn.rongcapital.chorus.das.entity.ProjectInfo;
import cn.rongcapital.chorus.das.entity.ProjectInfoDO;
import cn.rongcapital.chorus.das.entity.TUser;
import cn.rongcapital.chorus.das.entity.web.JobCause;
import cn.rongcapital.chorus.das.entity.web.JobMonitorCause;
import cn.rongcapital.chorus.das.service.JobMonitorService;
import cn.rongcapital.chorus.das.service.JobService;
import cn.rongcapital.chorus.das.service.ProjectInfoService;
import cn.rongcapital.chorus.das.service.ProjectMemberMappingService;
import cn.rongcapital.chorus.das.service.TUserService;
import cn.rongcapital.chorus.server.job.monitor.controller.vo.DetailJobMonitorVO;
import cn.rongcapital.chorus.server.job.monitor.controller.vo.JobMonitorVO;
import cn.rongcapital.chorus.server.project.controller.vo.ProjectInfoVo;
import cn.rongcapital.chorus.server.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Li.ZhiWei
 */
@Slf4j
@RestController
public class JobMonitorController {

    @Autowired
    private JobMonitorService jobMonitorService;

    @Autowired
    private JobService jobService;
    
    @Autowired
    private TUserService userService;
    
    @Autowired
    private ProjectInfoService projectInfoService;
    @Autowired
    private ProjectMemberMappingService projectMemberMappingService;

    @RequestMapping(value = "/{userId}/{projectId}/job/{executionId}/log/tailf")
    public @ResponseBody ResultVO<LogBO> realTimeLog(
            @PathVariable String userId, @PathVariable long projectId, @PathVariable long executionId,
            @RequestParam(defaultValue = "0") long lastTimestamp,// post form param or get url param
            @RequestBody(required = false) String lastTimestampJson,// post json data {"lastTimestamp":1504074057000}
            HttpServletRequest request
    ) {
        try {
            TUser user = userService.getUserById(userId);
            Assert.notNull(user, "invalid userId param " + userId);

            final ProjectInfo projectInfo = projectInfoService.selectByPrimaryKey(projectId);
            Assert.notNull(projectInfo, "invalid projectId param " + projectId);

            final ProjectInfoDO userProject = projectMemberMappingService.getProject(userId, projectId);
            Assert.notNull(userProject, "cannot found the project  " + projectId + " for user " + userId);

            int size = 1000; // show big enough to get all new log lines

            if (StringUtils.equalsIgnoreCase("POST", request.getMethod())) {
                final Long postDataParam = JSONObject.parseObject(lastTimestampJson).getLong("lastTimestamp");
                if (lastTimestamp == 0 && postDataParam != null) lastTimestamp = postDataParam;
            }

            if (lastTimestamp <= 0) size = 50; // 取最近的50条数据

            final LogBO logBO = jobMonitorService.realTimeLog(executionId, lastTimestamp, 0, size);
            return ResultVO.success(logBO);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
            return ResultVO.error(new ServiceException(StatusCode.SYSTEM_ERR, e));
        }
    }

    @RequestMapping(value = "/job/jobMonitor", method = RequestMethod.GET)
    public ModelAndView apply() {
        return new ModelAndView("dataDev/jobMonitorList");
    }

    @RequestMapping(value = "/job/jobMonitorList", method = RequestMethod.POST)
    @ResponseBody
    public ResultVO<PageInfo> getJobMonitorList(@RequestBody JobMonitorCause jobMonitorCause) {
        PageInfo pageInfo = new PageInfo();
        List<JobMonitorVO> list = new ArrayList<JobMonitorVO>();
        String userId = jobMonitorCause.getUserId();
        long totalNumber = 0;

        if (StringUtils.isNotBlank(userId)) {
            List<JobMonitor> jobMonitorListLimit = new ArrayList<>();
            Map<String, List<JobMonitor>> jobMonitorMap = jobMonitorService.searchJobMonitor(jobMonitorCause, userId);
            totalNumber = jobMonitorMap.get("jmListFilterByXD").size();

            if(totalNumber == 0){
                return ResultVO.error(StatusCode.JOBMONITOR_DATA_EMPTY);
            }
            jobMonitorListLimit = jobMonitorService.composeJobMonitorWithXdByLimit(
                    jobMonitorMap.get("jmListFilterByJob"), jobMonitorMap.get("jmListFilterByXD"), jobMonitorCause);

            for (JobMonitor jobMonitor : jobMonitorListLimit) {
                JobMonitorVO jobMonitorVO = OrikaBeanMapper.INSTANCE.map(jobMonitor, JobMonitorVO.class);
                jobMonitorVO.setFormatTime(jobMonitor.getJobStartTime(), jobMonitor.getJobStopTime());
                list.add(jobMonitorVO);
            }

            pageInfo.setPageSize(jobMonitorCause.getRowCnt());
            pageInfo.setPageNum(jobMonitorCause.getPage());
            pageInfo.setTotal(totalNumber);
            pageInfo.setList(list);
        } else {
            return ResultVO.error(StatusCode.JOBMONITOR_USER_ERROR);
        }

        return ResultVO.success(pageInfo);
    }

    @RequestMapping(value = "/job/filter/project", method = RequestMethod.POST)
    @ResponseBody
    public ResultVO<List<ProjectInfoVo>> getProjectInfoList(@RequestBody String userId) {
        List<ProjectInfoVo> resultList = new ArrayList<ProjectInfoVo>();
        List<ProjectInfo> projects = jobMonitorService.searchProjectInfoFromUser(userId);

        if (projects != null && projects.size() > 0) {
            for (ProjectInfo project : projects) {
                ProjectInfoVo projectInfoVo = OrikaBeanMapper.INSTANCE.map(project, ProjectInfoVo.class);
                resultList.add(projectInfoVo);
            }
        } else {
            return ResultVO.error(StatusCode.JOBMONITOR_USER_NONE_PROJECT_ERROR);
        }

        return ResultVO.success(resultList);
    }

    @RequestMapping(value = "/job/getSubJobMonitorList/{jobId}/{executionId}", method = RequestMethod.GET)
    @ResponseBody
    public ResultVO<DetailJobMonitorVO> getSubJobMonitorList(@PathVariable int jobId , @PathVariable long executionId) {
        DetailJobMonitorVO detailJobMonitorVO = new DetailJobMonitorVO();
        List<JobMonitorVO> list = new ArrayList<JobMonitorVO>();

        try {
            List<JobMonitor> subJobMonitor = jobMonitorService.getSubJobMonitor(jobId,executionId);
            Job job = jobService.selectJob(jobId);
            String workflowDSL = job.getWorkFlowDSL();

            if (subJobMonitor != null && subJobMonitor.size() > 0) {
                for (JobMonitor sjm : subJobMonitor) {
                    JobMonitorVO jobMonitorVO = OrikaBeanMapper.INSTANCE.map(sjm, JobMonitorVO.class);
                    jobMonitorVO.setFormatTime(sjm.getJobStartTime(), sjm.getJobStopTime());
                    list.add(jobMonitorVO);
                }
            }

            detailJobMonitorVO.setList(list);
            detailJobMonitorVO.setWorkFlowDSL(workflowDSL);
        } catch (Exception e) {
            log.error("Get subJobMonitorList exception ---->" + e.getMessage(), e);
            return ResultVO.error(StatusCode.JOBMONITOR_SERVICE_ERROR);
        }

        return ResultVO.success(detailJobMonitorVO);
    }

    @RequestMapping(value = "/job/streamMonitorList", method = RequestMethod.POST)
    @ResponseBody
    public ResultVO<PageInfo> getStreamMonitorList(@RequestBody JobMonitorCause jobMonitorCause) {

        PageInfo pageInfo = new PageInfo();
        List<JobMonitorVO> list = new ArrayList<JobMonitorVO>();
        String userId = jobMonitorCause.getUserId();
        long totalNumber = 0;

        if (StringUtils.isNotBlank(userId)) {

            JobCause jobCause = new JobCause();
            jobCause.setJobAliasName(jobMonitorCause.getJobAliasName());
            jobCause.setPage(jobMonitorCause.getPage());
            jobCause.setProjectId(jobMonitorCause.getProjectIds()[0]);
            jobCause.setUserId(userId);
            // 实时任务
            jobCause.setJobType(1);
            // 部署状态
            jobCause.setStatus("DEPLOY");
            List<Job> jobList = jobService.getProjectJobList(jobCause);

            if (jobList.size() > 0) {
                pageInfo.setPageSize(jobMonitorCause.getRowCnt());
                pageInfo.setPageNum(jobMonitorCause.getPage());
                pageInfo.setTotal(jobList.size());
                pageInfo.setList(jobList);
            }else{
                return ResultVO.error(StatusCode.JOBMONITOR_DATA_EMPTY);
            }
        } else {
            return ResultVO.error(StatusCode.JOBMONITOR_USER_ERROR);
        }

        return ResultVO.success(pageInfo);
    }

    @RequestMapping(value = "/job/streamMonitorDetail/{jobId} ", method = RequestMethod.GET)
    @ResponseBody
    public ResultVO<DetailJobMonitorVO> streamMonitorDetail(@PathVariable int jobId) {
        DetailJobMonitorVO detailJobMonitorVO = new DetailJobMonitorVO();

        try {
            Job job = jobService.selectJob(jobId);
            String workflowDSL = job.getWorkFlowDSL();
            long recordCount = jobMonitorService.getStreamRecordCount(job.getJobName());
            detailJobMonitorVO.setWorkFlowDSL(workflowDSL);
            detailJobMonitorVO.setRecordCount(recordCount);
        } catch (Exception e) {
            log.error("Get streamMonitorDetail exception ---->" + e.getMessage(), e);
            return ResultVO.error(StatusCode.JOBMONITOR_SERVICE_ERROR);
        }

        return ResultVO.success(detailJobMonitorVO);
    }

}
