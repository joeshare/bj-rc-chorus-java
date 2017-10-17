package cn.rongcapital.chorus.das.service;

import java.util.Map;

/**
 * 给指定项目成员发送邮件
 *
 * @author li.hzh
 */
public interface ProjectMemberMailSenderService {

    /**
     * 默认给项目Owner和管理员发送邮件
     *
     * @param projectId
     */
    void sendMail(Long projectId, String templateName, String subject, Map<String, Object> templateParamMap);

    /**
     * 默认给项目Owner和管理员发送邮件
     *
     * @param projectCode
     * @param templateName
     * @param subject
     * @param templateParamMap
     */
    void sendMail(String projectCode, String templateName, String subject, Map<String, Object> templateParamMap);
}
