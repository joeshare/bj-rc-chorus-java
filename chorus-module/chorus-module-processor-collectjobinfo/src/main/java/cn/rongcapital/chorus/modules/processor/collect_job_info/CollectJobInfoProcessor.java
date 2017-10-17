package cn.rongcapital.chorus.modules.processor.collect_job_info;

import cn.rongcapital.chorus.modules.processor.collect_job_info.dao.ChorusJobRepository;
import cn.rongcapital.chorus.modules.processor.collect_job_info.dao.ChorusModuleRepository;
import cn.rongcapital.chorus.modules.processor.collect_job_info.dao.ChorusProjectRepository;
import cn.rongcapital.chorus.modules.processor.collect_job_info.dao.ChorusTaskRepository;
import cn.rongcapital.chorus.modules.processor.collect_job_info.entity.ChorusJob;
import cn.rongcapital.chorus.modules.processor.collect_job_info.entity.ChorusModule;
import cn.rongcapital.chorus.modules.processor.collect_job_info.entity.ChorusProject;
import cn.rongcapital.chorus.modules.processor.collect_job_info.entity.ChorusTask;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.handler.AbstractReplyProducingMessageHandler;
import org.springframework.messaging.Message;

import java.util.List;

/**
 * Created by abiton on 09/08/2017.
 */
@Slf4j
public class CollectJobInfoProcessor extends AbstractReplyProducingMessageHandler {
    @Autowired
    ChorusJobRepository chorusJobRepository;
    @Autowired
    ChorusTaskRepository chorusTaskRepository;
    @Autowired
    ChorusProjectRepository chorusProjectRepository;
    @Autowired
    ChorusModuleRepository chorusModuleRepository;
    private static final String CHORUS_PREFIX = "chorus_";

    @Override
    protected Object handleRequestMessage(Message<?> message) {
        log.debug("collect processor receive message {}", message);
        JSONObject jsonObject = JSON.parseObject(message.getPayload().toString());
        String jobName = jsonObject.getString("jobName");
        ChorusJob chorusJob;
        ChorusTask chorusTask;
        //如果jobName以chorus_开头，说明它是包含一个module的job
        if (jobName.startsWith(CHORUS_PREFIX)) {
            chorusJob = chorusJobRepository.findByJobName(jobName);
            List<ChorusTask> chorusTasks = chorusTaskRepository.findByJobId(chorusJob.getJobId());
            log.debug("chorus job is {}", chorusJob);
            if (chorusTasks.size() == 1) {
                chorusTask = chorusTasks.get(0);
            } else {
                log.error("job {} should have only one task,but it has {}", jobName, chorusTasks);
                return null;
            }
        } else {
            //如果jobName不以chorus_开头，说明它是包含多个module的job,且jobName是其中的taskName
            chorusTask = chorusTaskRepository.findByTaskName(jobName);
            if (chorusTask == null){
                log.error("task {} does not exist",jobName);
                return null;
            }
            chorusJob = chorusJobRepository.findByJobId(chorusTask.getJobId());
        }
        ChorusProject chorusProject = chorusProjectRepository.findByProjectId(chorusJob.getProjectId());
        jsonObject.put("projectId", chorusJob.getProjectId());
        jsonObject.put("project", chorusProject.getProjectCode());
        jsonObject.put("projectName", chorusProject.getProjectName());
        jsonObject.put("jobId", chorusJob.getJobId());
        jsonObject.put("userName", chorusJob.getUserName());
        jsonObject.put("moduleName", chorusTask.getModuleName());
        if (StringUtils.isBlank(jsonObject.getString("moduleName"))) {
            return null;
        }
        ChorusModule chorusModule = chorusModuleRepository.findByModuleName(jsonObject.getString("moduleName"));
        if (chorusModule != null) {
            jsonObject.put("moduleViewName", chorusModule.getModuleViewName());
        } else {
            return null;
        }
        log.debug(jsonObject.toJSONString());
        return jsonObject.toJSONString();
    }
}

