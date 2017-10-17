package cn.rongcapital.chorus.monitor.springxd.queue;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import cn.rongcapital.chorus.common.email.MailSender;
import cn.rongcapital.chorus.common.email.MailSenderModel;
import cn.rongcapital.chorus.common.util.StringUtils;
import cn.rongcapital.chorus.das.entity.Job;
import cn.rongcapital.chorus.das.entity.ProjectInfo;
import cn.rongcapital.chorus.das.entity.ProjectInfoDO;
import cn.rongcapital.chorus.das.entity.TUser;
import cn.rongcapital.chorus.das.entity.TaskExecutionTimeout;
import cn.rongcapital.chorus.das.entity.caas.CaasUser;
import cn.rongcapital.chorus.das.service.CaasClient;
import cn.rongcapital.chorus.das.service.ProjectInfoService;
import cn.rongcapital.chorus.das.service.ProjectMemberMappingService;
import cn.rongcapital.chorus.das.service.TUserService;
import cn.rongcapital.chorus.das.service.TaskExecutionService;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yunzhong
 * @date 2017年5月25日下午1:53:00
 */
@Slf4j
@Component
public class ExecutionTimeoutHandler implements InitializingBean {
    private static final String MAIL_TEMPLATE_NAME = "job-execution-timeout.ftl";

    private static final String SEND_STATUS_SENDED = "SENDED";
    private static final String SEND_STATUS_FAILED = "FAILED";

    @Autowired
    private MailSender sender;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Autowired
    private TaskExecutionService executionService;

    @Autowired
    private CaasClient caasClient;

    @Autowired
    private ProjectInfoService projectService;

    @Autowired
    private TUserService userService;

    @Autowired
    private ProjectMemberMappingService memberService;

    @Override
    public void afterPropertiesSet() throws Exception {
        Thread timeoutThread = new Thread() {

            @Override
            public void run() {
                while (true) {
                    try {
                        TaskExecutionTimeout xdExecutionTimeout = null;
                        xdExecutionTimeout = ExecutionTimeoutQueue.pollTimeout();
                        if (xdExecutionTimeout == null || xdExecutionTimeout.getJob() == null) {
                            continue;
                        }
                        xdExecutionTimeout.setStatus(SEND_STATUS_SENDED);
                        Job job = xdExecutionTimeout.getJob();
                        xdExecutionTimeout.setJobName(job.getJobName());
                        try {
                            ProjectInfo project = projectService.selectByPrimaryKey(Long.valueOf(job.getProjectId()));
                            if (project == null) {
                                log.error("Failed to get project [" + job.getProjectId() + "] of job ["
                                        + job.getJobAliasName() + "]");
                                xdExecutionTimeout.setError("empty project.");
                                xdExecutionTimeout.setStatus(SEND_STATUS_FAILED);
                                executionService.insert(xdExecutionTimeout);
                                continue;
                            }
                            // 为task owner 发送邮件
                            TUser taskOwner = userService.getUserById(job.getCreateUser());
                            if (taskOwner != null) {
                                sendEmail(taskOwner.getUserName(), taskOwner.getEmail(), job, project,
                                        xdExecutionTimeout);
                            } else {
                                CaasUser caasTaskOwner = sendEmail(job.getCreateUserName(), job, project,
                                        xdExecutionTimeout);
                                if (caasTaskOwner != null) {
                                    cache(caasTaskOwner);
                                }
                            }
                            // 为project manager 发送邮件
                            final List<ProjectInfoDO> managers = memberService.selectMembers(project.getProjectId(),
                                    "5");
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
                                    sendEmail(manager.getUserName(), manager.getEmail(), job, project,
                                            xdExecutionTimeout);
                                }
                                if (!CollectionUtils.isEmpty(missed)) {
                                    assembleUserInfo(missed, project.getProjectCode(), "项目管理员");
                                    for (ProjectInfoDO miss : missed) {
                                        sendEmail(miss.getUserName(), miss.getEmail(), job, project,
                                                xdExecutionTimeout);
                                    }
                                }
                            }
                            log.info("send email .......");
                        } catch (Throwable e) {
                            log.error("Failed to send email of job [" + job.getJobAliasName() + "].", e);
                        }
                    } catch (Throwable e) {
                        log.warn("ExecutionAlertHandler execute uncaught exception.", e);
                    }
                }
            }

            private void cache(CaasUser caasTaskOwner) {
                TUser tUser = new TUser();
                tUser.setEmail(caasTaskOwner.getEmail());
                tUser.setUserId(caasTaskOwner.getCode());
                tUser.setUserName(caasTaskOwner.getName());
                userService.add(tUser);
            }

            /**
             * @param userId
             * @param job
             * @param project
             * @param timeoutExecution
             * @author yunzhong
             * @time 2017年8月21日上午10:17:51
             */
            public CaasUser sendEmail(String userId, Job job, ProjectInfo project,
                    TaskExecutionTimeout timeoutExecution) {
                if (StringUtils.isEmpty(userId)) {
                    log.error("User is null to send email of job [" + timeoutExecution.getJobName() + "]");
                    timeoutExecution.setError("User is null");
                    timeoutExecution.setStatus(SEND_STATUS_FAILED);
                    timeoutExecution.setEmail(null);
                    executionService.insert(timeoutExecution);
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
                    timeoutExecution.setStatus(SEND_STATUS_FAILED);
                    timeoutExecution.setError("Unkown user [" + userId + "]");
                    timeoutExecution.setEmail(null);
                    executionService.insert(timeoutExecution);
                    return user;
                }
                sendEmail(user.getName(), user.getEmail(), job, project, timeoutExecution);
                return user;
            }

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
                            cache(caasUser);
                        }
                    }
                } catch (Exception e) {
                    log.error("Failed to assemble user info of project " + projectCode + " code " + roleName + ".", e);
                    return;
                }

            }

            /**
             * @param user
             * @param job
             * @param project
             * @param timeoutExecution
             * @author yunzhong
             * @time 2017年8月22日上午9:17:22
             */
            public void sendEmail(String userName, String email, Job job, ProjectInfo project,
                    TaskExecutionTimeout timeoutExecution) {
                try {
                    timeoutExecution.setEmail(email);

                    MailSenderModel data = new MailSenderModel();
                    Template template = freeMarkerConfigurer.getConfiguration().getTemplate(MAIL_TEMPLATE_NAME);
                    Map<String, Object> model = new HashMap<>();
                    model.put("name", userName);
                    model.put("projectName", project.getProjectName());
                    model.put("taskName", job.getJobAliasName());
                    model.put("startTime", timeoutExecution.getStartTime());
                    model.put("expectEndTime", timeoutExecution.getExpectEndTime());

                    data.setContext(FreeMarkerTemplateUtils.processTemplateIntoString(template, model));
                    data.setSubject("任务执行超时通知");
                    data.setToAddr(email);
                    sender.send(data);
                    timeoutExecution.setStatus(SEND_STATUS_SENDED);
                    timeoutExecution.setError(null);
                    log.info("send email [" + email + "] .......");
                } catch (Throwable e) {
                    log.error("Failed to send email of job [" + timeoutExecution.getJobName() + "].", e);
                    timeoutExecution.setError(e.getLocalizedMessage());
                    timeoutExecution.setStatus(SEND_STATUS_FAILED);
                }
                executionService.insert(timeoutExecution);
            }
        };
        timeoutThread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread t, Throwable e) {
                log.error("execution timeout monitor queue thread uncaught exception ", e);
            }
        });
        timeoutThread.setName("executionTimeoutThread");
        timeoutThread.start();
    }

}
