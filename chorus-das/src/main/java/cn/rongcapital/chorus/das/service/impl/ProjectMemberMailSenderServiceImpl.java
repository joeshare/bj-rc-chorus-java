package cn.rongcapital.chorus.das.service.impl;

import cn.rongcapital.chorus.common.email.MailSender;
import cn.rongcapital.chorus.common.email.MailSenderModel;
import cn.rongcapital.chorus.common.util.StringUtils;
import cn.rongcapital.chorus.das.entity.*;
import cn.rongcapital.chorus.das.entity.caas.CaasUser;
import cn.rongcapital.chorus.das.service.*;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ProjectMemberMailSenderServiceImpl implements ProjectMemberMailSenderService {

    private static final String ROLE_MANAGER = "5";
    // CAAS接口匪夷所思！
    private static final String ROLE_OWNER = "4";

    @Autowired
    private ProjectMemberMappingService memberMappingService;
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;
    @Autowired
    private MailSender sender;
    @Autowired
    private CaasClient caasClient;
    @Autowired
    private ProjectInfoService projectInfoService;
    @Autowired
    private TUserService userService;

    @Override
    public void sendMail(Long projectId, String templateName, String subject, Map<String, Object> templateParamMap) {
        if (projectId == null) {
            log.warn("Project id is null. Can't send mail.");
            return;
        }
        // To Project Owner
        final List<ProjectInfoDO> owners = memberMappingService.selectMembers(projectId, ROLE_OWNER);
        // 为project manager 发送邮件
        final List<ProjectInfoDO> managers = memberMappingService.selectMembers(projectId, ROLE_MANAGER);
        owners.addAll(managers);
        final List<ProjectInfoDO> allMembers = owners;
        if (!CollectionUtils.isEmpty(allMembers)) {
            // 为所有的项目管理员发送邮件。如果本地没有user的详细信息，从caas同步。
            List<ProjectInfoDO> missed = new ArrayList<>();
            for (ProjectInfoDO member : allMembers) {
                if (StringUtils.isEmpty(member.getEmail())
                        || StringUtils.isEmpty(member.getUserName())) {
                    missed.add(member);
                    continue;
                }
                doSendEmail(member.getUserName(), member.getEmail(), templateName, subject, templateParamMap);
            }
            if (!CollectionUtils.isEmpty(missed)) {
                ProjectInfo project = projectInfoService.selectByPrimaryKey(projectId);
                if (project == null) {
                    log.warn("Can not find project info. Project id: {}.", projectId);
                    return;
                }
                for (ProjectInfoDO miss : missed) {
                    assembleUserInfo(missed, project.getProjectCode(), miss.getRoleName());
                    if (miss.getEmail() != null && !miss.getEmail().isEmpty()) {
                        doSendEmail(miss.getUserName(), miss.getEmail(), templateName, subject, templateParamMap);
                    }
                    try {
                        CaasUser caasUser = caasClient.getUser(miss.getUserName());
                        cacheUser(caasUser);
                    } catch (Exception e) {
                        log.error("Get caas user {} error.", miss.getUserName());
                        log.error("", e);
                    }
                }
            }
        }
    }

    @Override
    public void sendMail(String projectCode, String templateName, String subject, Map<String, Object> templateParamMap) {
        if (projectCode == null || projectCode.isEmpty()) {
            log.warn("Project code is null, can not send mail.");
            return;
        }
        ProjectInfo projectInfo = projectInfoService.selectByProjectCode(projectCode);
        if (projectInfo == null) {
            log.warn("Project [{}] does not exist, can not sedn mail", projectCode);
            return;
        }
        sendMail(projectInfo.getProjectId(), templateName, subject, templateParamMap);
    }


    private void cacheUser(CaasUser caasTaskOwner) {
        TUser tUser = new TUser();
        tUser.setEmail(caasTaskOwner.getEmail());
        tUser.setUserId(caasTaskOwner.getCode());
        tUser.setUserName(caasTaskOwner.getName());
        userService.add(tUser);
    }

    /**
     * 通过caas填充user的name和email
     *
     * @param missed
     * @param projectCode
     * @param roleName
     * @author yunzhong
     * @time 2017年8月22日下午2:39:54
     */
    private void assembleUserInfo(List<ProjectInfoDO> missed, String projectCode, String roleName) {
        try {
            final List<CaasUser> projectUsers = caasClient.getProjectUsers(projectCode, roleName);
            if (CollectionUtils.isEmpty(projectUsers)) {
                log.warn("Failed to get project {} users of role {} from caas.", projectCode, roleName);
                return;
            }
            Map<String, CaasUser> tempMap = new HashMap<>();
            for (CaasUser projectUser : projectUsers) {
                tempMap.put(projectUser.getCode(), projectUser);
            }
            for (ProjectInfoDO miss : missed) {
                if (tempMap.containsKey(miss.getUserId())) {
                    final CaasUser caasUser = tempMap.get(miss.getUserId());
                    miss.setUserName(caasUser.getName());
                    miss.setEmail(caasUser.getEmail());
                }
            }
        } catch (Exception e) {
            log.error("Failed to assemble user info of project " + projectCode + " code " + roleName + ".", e);
            return;
        }

    }

    private void doSendEmail(String userName, String emailAddress, String templateName, String subject, Map<String, Object> paramMap) {
        try {
            paramMap.put("name", userName);
            MailSenderModel data = new MailSenderModel();
            Template template = freeMarkerConfigurer.getConfiguration().getTemplate(templateName);

            data.setContext(FreeMarkerTemplateUtils.processTemplateIntoString(template, paramMap));
            data.setSubject(subject);
            data.setToAddr(emailAddress);
            sender.send(data);
            log.info("send email [" + emailAddress + "] .......");
        } catch (Throwable e) {
            log.error("Send mail to {} error.", emailAddress);
            log.error("", e);
        }
    }

}
