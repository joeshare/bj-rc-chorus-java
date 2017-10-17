package cn.rongcapital.chorus.monitor.springxd.queue;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.rongcapital.chorus.das.entity.caas.CaasUser;
import cn.rongcapital.chorus.das.service.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import cn.rongcapital.chorus.common.util.StringUtils;
import cn.rongcapital.chorus.das.entity.Job;
import cn.rongcapital.chorus.das.entity.ProjectInfo;
import cn.rongcapital.chorus.das.entity.ProjectInfoDO;
import cn.rongcapital.chorus.das.entity.TUser;
import cn.rongcapital.chorus.das.entity.XDExecution;
import cn.rongcapital.chorus.common.email.MailSender;
import cn.rongcapital.chorus.common.email.MailSenderModel;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yunzhong
 * @date 2017年5月25日下午1:53:00
 */
@Slf4j
@Component
public class ExecutionAlertHandler implements InitializingBean {
    private static final String MAIL_TEMPLATE_NAME = "job-execution-notify.ftl";

    private static final String SEND_STATUS_SENDED = "SENDED";
    private static final String SEND_STATUS_FAILED = "FAILED";
    @Autowired
    private MailSender sender;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Autowired
    private JobFailHistoryService jobFailHistoryService;

    @Autowired
    private CaasClient caasClient;

    @Autowired
    private JobService jobService;

    @Autowired
    private ProjectInfoService projectService;

    @Autowired
    private ProjectMemberMappingService memberService;

    @Autowired
    private TUserService userService;

    @Override
    public void afterPropertiesSet() throws Exception {
        Thread alertThread = new Thread() {

            @Override
            public void run() {
                while (true) {
                    try {
                        XDExecution xdExecution = null;
                        xdExecution = ExecutionAlertQueue.pollTimeout();
                        if (xdExecution == null) {
                            continue;
                        }
                        xdExecution.setStatus(SEND_STATUS_SENDED);
                        // 如果是job无法在chorus中找到相关信息，则记录发送邮件失败。
                        Job job = jobService.selectJobByName(xdExecution.getJobName());
                        if (job == null) {
                            log.error("There is no job with name [" + xdExecution.getJobName() + "]");
                            xdExecution.setError("There is no job [" + xdExecution.getJobName() + "]");
                            xdExecution.setStatus(SEND_STATUS_FAILED);
                            jobFailHistoryService.insert(xdExecution);
                            continue;
                        }
                        ProjectInfo project = projectService.selectByPrimaryKey(Long.valueOf(job.getProjectId()));
                        if (project == null) {
                            log.error("Failed to get project [" + job.getProjectId() + "] of job ["
                                    + job.getJobAliasName() + "]");
                            xdExecution.setError("unkown project [" + job.getProjectId() + "]");
                            xdExecution.setStatus(SEND_STATUS_FAILED);
                            jobFailHistoryService.insert(xdExecution);
                            continue;
                        }
                        // 为task owner 发送邮件
                        TUser taskOwner = userService.getUserById(job.getCreateUser());
                        if (taskOwner != null) {
                            sendEmail(taskOwner.getUserName(), taskOwner.getEmail(), job, project, xdExecution);
                        } else {
                            CaasUser caasTaskOwner = sendEmail(job.getCreateUserName(), job, project, xdExecution);
                            if (caasTaskOwner != null) {
                                cacheUser(caasTaskOwner);
                            }
                        }
                        // 为project manager 发送邮件
                        final List<ProjectInfoDO> managers = memberService.selectMembers(project.getProjectId(), "5");
                        if (!CollectionUtils.isEmpty(managers)) {// 为所有的项目管理员发送邮件。
                                                                 // jobowner已经发送邮件，不再发送。如果本地没有user的详细信息，从caas同步。
                            List<ProjectInfoDO> missed = new ArrayList<>();
                            for (ProjectInfoDO manager : managers) {
                                if (job.getCreateUser().equals(manager.getUserId())) {
                                    continue;
                                }
                                if (StringUtils.isEmpty(manager.getEmail())
                                        || StringUtils.isEmpty(manager.getUserName())) {
                                    missed.add(manager);
                                    continue;
                                }
                                sendEmail(manager.getUserName(), manager.getEmail(), job, project, xdExecution);
                            }
                            if (!CollectionUtils.isEmpty(missed)) {
                                assembleUserInfo(missed, project.getProjectCode(), "项目管理员");
                                for (ProjectInfoDO miss : missed) {
                                    sendEmail(miss.getUserName(), miss.getEmail(), job, project, xdExecution);
                                }
                            }
                        }

                    } catch (Throwable e) {
                        log.warn("ExecutionAlertHandler execute uncaught exception.", e);
                    }
                }
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
                            cacheUser(caasUser);
                        }
                    }
                } catch (Exception e) {
                    log.error("Failed to assemble user info of project " + projectCode + " code " + roleName + ".", e);
                    return;
                }

            }

            /**
             * @param userId
             * @param job
             * @param project
             * @param xdExecution
             * @author yunzhong
             * @time 2017年8月21日上午10:17:51
             */
            public CaasUser sendEmail(String userId, Job job, ProjectInfo project, XDExecution xdExecution) {
                if (StringUtils.isEmpty(userId)) {
                    log.error("User is null to send email of job [" + xdExecution.getJobName() + "]");
                    xdExecution.setEmail(null);
                    xdExecution.setError("create User is null");
                    xdExecution.setStatus(SEND_STATUS_FAILED);
                    jobFailHistoryService.insert(xdExecution);
                    return null;
                }
                CaasUser user = null;
                try {
                    user = caasClient.getUser(userId);
                } catch (Exception e) {
                    log.error("Get user [" + userId + "] email exception of job [" + job.getJobAliasName() + "].", e);
                }
                if (user == null) {
                    log.error("Failed to get user email [" + userId + "] from caas.");
                    xdExecution.setEmail(null);
                    xdExecution.setStatus(SEND_STATUS_FAILED);
                    xdExecution.setError("Unkown user [" + userId + "]");
                    jobFailHistoryService.insert(xdExecution);
                    return user;
                }
                sendEmail(user.getName(), user.getEmail(), job, project, xdExecution);
                return user;
            }

            /**
             * @param user
             * @param job
             * @param project
             * @param xdExecution
             * @author yunzhong
             * @time 2017年8月22日上午9:17:22
             */
            public void sendEmail(String userName, String email, Job job, ProjectInfo project,
                    XDExecution xdExecution) {
                try {
                    xdExecution.setEmail(email);

                    MailSenderModel data = new MailSenderModel();
                    Template template = freeMarkerConfigurer.getConfiguration().getTemplate(MAIL_TEMPLATE_NAME);
                    Map<String, Object> model = new HashMap<>();
                    model.put("name", userName);
                    model.put("projectName", project.getProjectName());
                    model.put("taskName", job.getJobAliasName());
                    model.put("startTime", xdExecution.getStartTime());
                    model.put("endTime", xdExecution.getEndTime());
                    model.put("status", "fail");

                    data.setContext(FreeMarkerTemplateUtils.processTemplateIntoString(template, model));
                    data.setSubject("任务执行失败通知");
                    data.setToAddr(email);
                    sender.send(data);
                    xdExecution.setStatus(SEND_STATUS_SENDED);
                    xdExecution.setError(null);
                    log.info("send email [" + email + "] .......");
                } catch (Throwable e) {
                    log.error("Failed to send email of job [" + xdExecution.getJobName() + "].", e);
                    xdExecution.setError(e.getLocalizedMessage());
                    xdExecution.setStatus(SEND_STATUS_FAILED);
                }
                jobFailHistoryService.insert(xdExecution);
            }
        };
        alertThread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread t, Throwable e) {
                log.error("alert monitor queue thread uncaught exception ", e);
            }
        });
        alertThread.setName("executionAlertHandlerThread");
        alertThread.start();
    }

}
