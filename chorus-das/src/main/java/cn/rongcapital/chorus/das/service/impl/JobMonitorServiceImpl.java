package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.common.xd.XDClient;
import cn.rongcapital.chorus.das.bo.LogBO;
import cn.rongcapital.chorus.das.dao.JobMapper;
import cn.rongcapital.chorus.das.dao.JobMonitorMapper;
import cn.rongcapital.chorus.das.dao.TaskMapper;
import cn.rongcapital.chorus.das.entity.Job;
import cn.rongcapital.chorus.das.entity.JobMonitor;
import cn.rongcapital.chorus.das.entity.ProjectInfo;
import cn.rongcapital.chorus.das.entity.Task;
import cn.rongcapital.chorus.das.entity.XDExecution;
import cn.rongcapital.chorus.das.entity.web.JobMonitorCause;
import cn.rongcapital.chorus.das.entity.web.TaskCause;
import cn.rongcapital.chorus.das.service.JobMonitorService;
import cn.rongcapital.chorus.das.xd.dao.XDMapper;
import com.alibaba.fastjson.JSONPath;
import com.google.common.collect.ImmutableList;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.search.sort.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.xd.rest.client.JobOperations;
import org.springframework.xd.rest.client.SpringXDOperations;
import org.springframework.xd.rest.client.impl.SpringXDException;
import org.springframework.xd.rest.domain.JobExecutionInfoResource;
import org.springframework.xd.rest.domain.metrics.CounterResource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Li.ZhiWei
 */
@Service(value = "JobMonitorService")
@Transactional
public class JobMonitorServiceImpl implements JobMonitorService {

    @Autowired
    private JobMonitorMapper jobMonitorMapper;
    @Autowired
    private JobMapper jobMapper;
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private XDMapper xdMapper;
    @Autowired
    private XDClient xdClient;
    @Autowired
    private JestClient jestClient;
    @Value("${elastic.execution.index.typs}")
    private Collection<String> indexTypes;
    @Value("${elastic.execution.index.names}")
    private Collection<String> indexNames;

    private final int MAX_LOG_LINE_NUM =1000;
    private final int DEFAULT_LOG_LINE_NUM = 100;

    @Override
    public Map<String, List<JobMonitor>> searchJobMonitor(JobMonitorCause jobMonitorCause, String userId) {
        Map<String, List<JobMonitor>> resultMap = new HashMap<>();
        List<JobMonitor> jmListFilterByJob = getAllJobMonitorWithJobCondition(jobMonitorCause, userId);
        List<JobMonitor> jmListFilterByXD = null;
        if (CollectionUtils.isEmpty(jmListFilterByJob)) {
            jmListFilterByXD = new ArrayList<>();
        } else {
            jmListFilterByXD = getAllJobMonitorWithXDCondition(jmListFilterByJob, jobMonitorCause);
        }

        resultMap.put("jmListFilterByJob", jmListFilterByJob);
        resultMap.put("jmListFilterByXD", jmListFilterByXD);
        return resultMap;
    }

    @Override
    public List<Long> getSubJobExecutionIds(long executionId) {
        SpringXDOperations springXDTemplate = xdClient.getSpringXDTemplate();
        JobOperations jobOperations = springXDTemplate.jobOperations();
        JobExecutionInfoResource jobExecution = jobOperations.displayJobExecution(executionId);
        List<JobExecutionInfoResource> childJobExecutions = jobExecution.getChildJobExecutions();
        List<Long> childJobExecutionIds = new ArrayList<>();

        if (childJobExecutions != null && childJobExecutions.size() > 0) {
            for (JobExecutionInfoResource childJob : childJobExecutions) {
                childJobExecutionIds.add(childJob.getExecutionId());
            }

            return childJobExecutionIds;
        }

        return null;
    }

    @Override
    public List<JobMonitor> getSubJobMonitor(int jobId, long executionId) {
        SpringXDOperations springXDTemplate = xdClient.getSpringXDTemplate();
        JobOperations jobOperations = springXDTemplate.jobOperations();
        JobExecutionInfoResource jobExecution = jobOperations.displayJobExecution(executionId);
        List<JobExecutionInfoResource> childJobExecutions = jobExecution.getChildJobExecutions();
        List<JobMonitor> childJobExecutionList = new ArrayList<>();

        if (childJobExecutions != null && childJobExecutions.size() > 0) {
            for (JobExecutionInfoResource childJob : childJobExecutions) {
                JobMonitor jm = generateJMObj(childJob);
                Task task = getTask(jobId, jm.getJobName());

                if (task != null) {
                    jm.setJobAliasName(task.getAliasName());
                    jm.setModuleName(task.getModuleName());
                    jm.setModuleType(task.getModuleType());
                }
                childJobExecutionList.add(jm);
            }

        } else {
            JobMonitor jm = generateJMObj(jobExecution);
            Task singleStep = taskMapper.getTaskList(jobId).get(0);
            jm.setJobAliasName(singleStep.getAliasName());
            jm.setJobName(singleStep.getTaskName());
            jm.setModuleName(singleStep.getModuleName());
            jm.setModuleType(singleStep.getModuleType());
            childJobExecutionList.add(jm);
        }

        return childJobExecutionList;
    }

    @Override
    public List<JobMonitor> searchJobMonitorWithJobId(JobMonitorCause jobMonitorCause) {
        Job job = jobMapper.selectJob(jobMonitorCause.getJobId());
        List<Job> jobList = new ArrayList<>();
        jobList.add(job);
        List<JobMonitor> jmList = jobMonitorMapper.selectAllWithJobId(jobList);

        return jmList;
    }

    @Override
    public List<ProjectInfo> searchProjectInfoFromUser(String userId) {
        List<ProjectInfo> projects = new ArrayList<>();
        List<ProjectInfo> list = jobMonitorMapper.getProjectRelatedWithUser(userId);

        if (list != null && list.size() > 0) {
            projects = list;
        }
        return projects;
    }

    @Override
    public int modifyUserProjectFilterState(String userId, JobMonitorCause jobMonitorCause) {
        List<ProjectInfo> projectRelatedWithUser = jobMonitorMapper.getProjectRelatedWithUser(userId);
        Long[] projectIdsFromPage = jobMonitorCause.getProjectIds();
        int returnCode = 0;

        if (projectIdsFromPage != null && projectIdsFromPage.length > 0) {
            jobMonitorMapper.modifyProjectFilterState(projectIdsFromPage, "Y", userId);
            List<Long> projectsStateWithY = Arrays.asList(projectIdsFromPage);
            Long[] projectsStateWithN = projectRelatedWithUser.stream()
                    .filter(p -> !projectsStateWithY.contains(p.getProjectId())).map(ProjectInfo::getProjectId)
                    .collect(Collectors.toList()).toArray(new Long[0]);

            if (projectsStateWithN.length > 0) {
                returnCode = jobMonitorMapper.modifyProjectFilterState(projectsStateWithN, "N", userId);
            }
        } else {
            Long[] projectsStateWithN = projectRelatedWithUser.stream().map(ProjectInfo::getProjectId)
                    .collect(Collectors.toList()).toArray(new Long[0]);
            returnCode = jobMonitorMapper.modifyProjectFilterState(projectsStateWithN, "N", userId);
        }

        return returnCode;
    }

    /**
     * 将xd中获取的execution信息与job信息合并生成任务监控对象
     * 
     * @param jmListFilterByXD 任务监控对象集合
     * @param jmListFilterByJob job表映射的任务监控对象
     * @param jobMonitorCause
     */
    public List<JobMonitor> composeJobMonitorWithXdByLimit(List<JobMonitor> jmListFilterByJob,
            List<JobMonitor> jmListFilterByXD, JobMonitorCause jobMonitorCause) {
        List<JobMonitor> jobMonitors = new ArrayList<>();

        if (jmListFilterByXD.size() > 0 && jmListFilterByJob.size() > 0) {
            jobMonitors = xdMapper.getLimitJobMonitorFromXDExecutions(jmListFilterByXD, jobMonitorCause);

            for (JobMonitor jm : jmListFilterByJob) {
                for (JobMonitor jobMonitor : jobMonitors) {
                    if (jobMonitor.getJobName().equals(jm.getJobName())) {
                        jobMonitor.setJobId(jm.getJobId());
                        jobMonitor.setJobAliasName(jm.getJobAliasName());
                        jobMonitor.setJobStatus(jm.getJobStatus());
                        jobMonitor.setJobDescription(jm.getJobDescription());
                    }
                }
            }
        }

        return jobMonitors;
    }

    private Task getTask(int jobId, String stepName) {
        TaskCause stepCause = new TaskCause();
        stepCause.setJobId(jobId);
        stepCause.setTaskName(stepName);
        Task task = taskMapper.selectTaskByName(stepCause);

        if (task != null) {
            return task;
        }

        return null;
    }

    private JobMonitor generateJMObj(JobExecutionInfoResource jobExecution) {
        JobMonitor jm = new JobMonitor();
        Long executeId = jobExecution.getJobExecution().getId();
        Long instanceId = jobExecution.getJobExecution().getJobInstance().getId();
        Date startTime = jobExecution.getJobExecution().getStartTime();
        Date endTime = jobExecution.getJobExecution().getEndTime();
        String executeStatus = jobExecution.getJobExecution().getStatus().toString();
        String jobName = jobExecution.getJobExecution().getJobInstance().getJobName();

        jm.setJobExecutionId(executeId);
        jm.setJobInstanceId(instanceId);
        jm.setJobStartTime(startTime);
        jm.setJobStopTime(endTime);
        jm.setJobExecuteStatus(executeStatus);
        jm.setJobName(jobName);
        return jm;
    }

    /**
     * 根据Job相关的查询条件获取 任务监控对象 所需属性
     * 
     * @param jobMonitorCause
     * @param userId
     */
    private List<JobMonitor> getAllJobMonitorWithJobCondition(JobMonitorCause jobMonitorCause, String userId) {
        List<Job> jobList = new ArrayList<>();
        List<JobMonitor> jmList = new ArrayList<>();
        getAllJobFromUser(userId, jobList);
        String aliasName = jobMonitorCause.getJobAliasName();
        Long[] projectIds = jobMonitorCause.getProjectIds();

        if (jobList.size() > 0 && projectIds != null && projectIds.length > 0) {
            if (aliasName != null && !(aliasName.equals(""))) {
                jmList = jobMonitorMapper.selectAllWithAliasNameAndJob(aliasName, jobList, projectIds);
            } else {
                jmList = jobMonitorMapper.selectAllWithJob(jobList, projectIds);
            }
        }

        return jmList;
    }

    /**
     * 根据XD Execution相关的查询条件获取 任务监控对象 所需属性
     * 
     * @param jmList
     * @param jobMonitorCause
     */
    private List<JobMonitor> getAllJobMonitorWithXDCondition(List<JobMonitor> jmList, JobMonitorCause jobMonitorCause) {
        List<JobMonitor> jobMonitorsFromXd = new ArrayList<>();
        jobMonitorsFromXd = xdMapper.getJobMonitorFromXDExecutions(jmList, jobMonitorCause);
        return jobMonitorsFromXd;
    }

    /**
     * 根据user获取所相关联项目中的所有任务
     * 
     * @param userId
     * @param jobList 置入此用户所关联的所有任务
     */
    private void getAllJobFromUser(String userId, List<Job> jobList) {
        List<ProjectInfo> projects = jobMonitorMapper.getProjectRelatedWithUser(userId);

        for (ProjectInfo project : projects) {
            List<Job> jobs = jobMapper.getJobRelatedWithProject(project);

            if (jobs.size() > 0) {
                jobList.addAll(jobs);
            }
        }
    }

    @Override
    public long getStreamRecordCount(String streamName) {
        long countData = 0;
        try {
            String counterName = streamName + "_counter";
            SpringXDOperations springXDTemplate = xdClient.getSpringXDTemplate();
            CounterResource counterResource = springXDTemplate.counterOperations().retrieve(counterName);
            countData = counterResource.getValue();
        } catch (SpringXDException e) {
            // 如果Stream 获取Counter失败 则返回Counter的值为0
        }

        return countData;
    }

    @Override
    public List<XDExecution> getXDExecutions(int pageNum, int pageSize, List<String> statusList) {

        return xdMapper.getXDExecutions((pageNum - 1) * pageSize, pageSize, statusList);
    }

    @Override
    public LogBO realTimeLog(long executionId, long startTimestamp, int from, int size) throws IOException {
        if (from < 0) from = 0;
        if (size < 0 || size > MAX_LOG_LINE_NUM) {
            size = DEFAULT_LOG_LINE_NUM;
        }
        //the size should set bigger enough to get all message at once, because more data is coming at the same time. there may be has a implicitly issue
        String query = buildQueryString(executionId, startTimestamp, from, size);
        Search search = new Search.Builder(query).addSort(new Sort("timestamp", Sort.Sorting.DESC))
                                                 .addType(indexTypes)
                                                 .addIndex(indexNames).build();
        final SearchResult result = jestClient.execute(search);
        final Integer _tmp = result.getTotal();
        final Integer total = _tmp == null ? 0 : _tmp;
        List<String> messages = ImmutableList.of();
        Long end = startTimestamp;
        if (total > 0) {
            messages = (List<String>) JSONPath.read(result.getJsonString(), "$.._source.message");
            List<String> timestamp = (List<String>) JSONPath.read(result.getJsonString(), "$.._source.timestamp");
            end = timestamp.stream().map(Long::valueOf).max(Long::compareTo).get();
        }
        return LogBO.builder().start(startTimestamp).end(end).lines(messages).total(total).build();
    }

    private String buildQueryString(long executionId, long startTimestamp, int from, int size) {
        return "{\"query\":{\"bool\":{\"must\":[{\"term\":{\"executionId\":\""
                       + executionId + "\"}},{\"range\":{\"timestamp\":{\"gt\":\""
                       + startTimestamp + "\"}}}]}},\"from\":"
                       + from + ",\"size\":"
                       + size + "}";
    }
}
