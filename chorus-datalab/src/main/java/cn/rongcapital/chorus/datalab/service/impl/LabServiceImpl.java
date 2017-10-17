package cn.rongcapital.chorus.datalab.service.impl;

import cn.rongcapital.chorus.common.constant.StatusCode;
import cn.rongcapital.chorus.common.exception.ServiceException;
import cn.rongcapital.chorus.das.entity.DatalabInfo;
import cn.rongcapital.chorus.das.entity.ProjectInfo;
import cn.rongcapital.chorus.das.service.DatalabInfoService;
import cn.rongcapital.chorus.das.service.ProjectInfoService;
import cn.rongcapital.chorus.datalab.service.LabService;
import cn.rongcapital.chorus.datalab.service.LabStatusService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * Created by abiton on 06/03/2017.
 */
@Service("LabService")
@Slf4j
public class LabServiceImpl implements LabService {
    @Value("${datalab.proxy.command}")
    private String command;
    @Value("${datalab.git}")
    private String gitUrl;
    @Value("${datalab.appmaster.uri}")
    private String appMasterUri;
    @Value("${datalab.yarnclient.uri}")
    private String yarnClientUri;
    @Value("${datalab.zeppelin.uri}")
    private String zeppelinUri;
    @Autowired
    private YarnClient yarnClient;
    @Autowired
    private LabStatusService labStatusService;
    @Autowired
    private DatalabInfoService datalabInfoService;

    @Autowired
    private ProjectInfoService projectInfoService;

    DefaultExecutor executor = new DefaultExecutor();

    @Override
    public void createLab(DatalabInfo datalabInfo) {
        ProjectInfo projectInfo = projectInfoService.selectByProjectCode(datalabInfo.getProjectCode());
        String userName = projectInfo.getUserName();
        if (StringUtils.isNotBlank(userName)){
            datalabInfo.setCreateUserName(userName);
        }
        datalabInfoService.create(datalabInfo);
    }

    @Override
    public void startLab(String projectCode, String labCode) {
        try {
            boolean submitted = isApplicationSubmitted(getProjectLabCode(projectCode, labCode));
            if (submitted) {
                return;
            }
        } catch (IOException e) {
            log.error("yarn connect error ", e);
            throw new ServiceException(StatusCode.DATALAB_START_ERROR);
        } catch (YarnException e) {
            log.error("yarn exception ", e);
            throw new ServiceException(StatusCode.DATALAB_START_ERROR);
        }

        boolean alive = labStatusService.isAlive(projectCode, labCode);
        if (alive) {
            return;
        }

        DatalabInfo datalabInfo = datalabInfoService.get(projectCode, labCode);

        CommandLine cmdLine = CommandLine.parse(command);
        cmdLine.addArgument(datalabInfo.getCreateUserName());
        String createCommand = "yarn jar " + yarnClientUri + " ";
        createCommand += " cn.rongcapital.chorus.datalab.client.ZeppelinYarnClient ";
        createCommand += " " + gitUrl;
        createCommand += " " + getProjectLabCode(projectCode, labCode);
        createCommand += " " + appMasterUri;
        createCommand += " " + datalabInfo.getMemory() + " " + datalabInfo.getCpu();
        createCommand += " " + zeppelinUri;
        cmdLine.addArgument(createCommand, false);
        try {
            int execute = executor.execute(cmdLine);
            if (execute != 0) {
                throw new ServiceException(StatusCode.DATALAB_START_ERROR);
            }
        } catch (IOException e) {
            log.error("create datalab error ", e);
            throw new ServiceException(StatusCode.DATALAB_START_ERROR);
        }
    }

    @Override
    public void stopLab(String projectCode, String labCode) {

        String status = labStatusService.getStatus(projectCode, labCode);
        if (status != null) {
            JSONObject jsonObject = JSON.parseObject(status);
            String applicationId = jsonObject.getString("applicationId");
            labStatusService.delete(projectCode, labCode);
            try {
                yarnClient.killApplication(getApplicationId(applicationId));
            } catch (YarnException e) {
                log.error("kill aplication " + applicationId + " error ", e);
                throw new ServiceException(StatusCode.DATALAB_STOP_ERROR);
            } catch (IOException e) {
                log.error("kill aplication " + applicationId + " error ", e);
                throw new ServiceException(StatusCode.DATALAB_STOP_ERROR);
            }
        }
    }

    @Override
    public void deleteLab(String projectCode, String labCode) {

        stopLab(projectCode, labCode);
        datalabInfoService.delete(projectCode, labCode);

    }

    @Override
    public List<DatalabInfo> getLabsByProject(String projectCode) {
        return datalabInfoService.get(projectCode);
    }

    @Override
    public List<DatalabInfo> getLabsByProject(String projectCode, int pageNum, int pageSize) {
        if (pageNum < 0 || pageSize < 0) {
            return Collections.emptyList();
        }
        return datalabInfoService.get(projectCode, pageNum, pageSize);
    }

    private ApplicationId getApplicationId(String applicationId) {

        String[] split = applicationId.split("_");
        return ApplicationId.newInstance(Long.parseLong(split[1]), Integer.parseInt(split[2]));
    }

    private String getProjectLabCode(String projectCode, String labCode) {
        return projectCode + "-" + labCode;
    }

    private boolean isApplicationSubmitted(String applicationName) throws IOException, YarnException {

        Set<String> types = new HashSet<>();
        types.add("dataLab");
        EnumSet<YarnApplicationState> statuses = EnumSet.of(YarnApplicationState.NEW,
                YarnApplicationState.NEW_SAVING, YarnApplicationState.SUBMITTED,
                YarnApplicationState.ACCEPTED, YarnApplicationState.RUNNING);

        List<ApplicationReport> applications = yarnClient.getApplications(types, statuses);

        if (!applications.isEmpty()) {
            for (ApplicationReport report : applications) {
                if (applicationName.equals(report.getName())) {
                    return true;
                }
            }

        }
        return false;
    }
}
