package cn.rongcapital.chorus.monitor.ranger;

import cn.rongcapital.chorus.das.entity.ProjectInfo;
import cn.rongcapital.chorus.das.service.AuthorizationDetailService;
import cn.rongcapital.chorus.das.service.ProjectMemberMailSenderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Component
public class RangerAuditDeniedHandler implements RangerAuditResponseHandler, InitializingBean {

    private static final String MAIL_TEMPLATE_NAME = "ranger-audit-notify.ftl";
    private static final String MAIL_SUBJECT = "非法数据访问通知";

    private static final AuditMailQueue queue = new AuditMailQueue();
    private Thread mailSendThread;
    @Autowired
    private AuthorizationDetailService authorizationDetailService;
    @Autowired
    private ProjectMemberMailSenderService mailSenderService;

    @Override
    public void handleResponse(QueryResponse response) {
        SolrDocumentList documents = response.getResults();
        log.info("Get {} denied audit records.", documents.getNumFound());
        documents.stream().forEach(document -> {
            queue.add(convertToModel(document));
        });
    }

    private RangerAuditModel convertToModel(SolrDocument document) {
        RangerAuditModel model = new RangerAuditModel();
        document.entrySet().stream().forEach(r -> {
                    setFieldValue(r.getKey(), r.getValue(), model);
                }
        );
        return model;
    }

    // 私有方法，不校验属性是否存在
    private void setFieldValue(String key, Object value, RangerAuditModel model) {
        Method method = findValueSetMethod(key, value);
        if (method == null) {
            log.debug("Can not set value of field {}. Set method not found.", key);
            return;
        }
        try {
            log.debug("Set value {} of field {}.", value, key);
            method.setAccessible(true);
            method.invoke(model, value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            //may never happen
            log.error("Dammit!", e);
        }
    }

    private Method findValueSetMethod(String key, Object value) {
        return BeanUtils.findMethod(RangerAuditModel.class, "set" + StringUtils.capitalize(key), value.getClass());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        mailSendThread = new Thread(() -> {
            log.info("Start audit mail send thread.");
            while (!Thread.currentThread().isInterrupted()) {
                RangerAuditModel auditModel = queue.poll();
                if (auditModel != null) {
                    log.info("Handle permission denied audit log {}.", auditModel);
                    PolicyProjectFinder finder = PolicyProjectFinderFactory.getFinder(auditModel.getRepo());
                    if (finder == null) {
                        log.info("Repo type {} not support yet. Don't notify any one.", auditModel.getRepo());
                        continue;
                    }
                    ProjectInfo projectInfo = finder.find(auditModel);
                    if (projectInfo == null) {
                        log.warn("Can not send mail. No project found for audit log {}. ", auditModel);
                        continue;
                    }
                    log.info("Found project {} for audit {}.", projectInfo.getProjectCode(), auditModel.getResource());
                    Map<String, Object> paramMap = createParamMap(auditModel);
                    mailSenderService.sendMail(projectInfo.getProjectId(), MAIL_TEMPLATE_NAME, MAIL_SUBJECT, paramMap);
                }
            }
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                log.error("Audit mail send thread interrupted.", e);
            }
        });
        mailSendThread.setName("Audit-Mail-Send-Thread");
        mailSendThread.start();
    }

    private Map<String, Object> createParamMap(RangerAuditModel auditModel) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("evtTime", auditModel.getEvtTime());
        paramMap.put("reqUser", auditModel.getReqUser());
        paramMap.put("action", auditModel.getAction());
        paramMap.put("resource", auditModel.getResource());
        paramMap.put("resType", auditModel.getResType());
        return paramMap;
    }

    private static class AuditMailQueue extends LinkedBlockingQueue<RangerAuditModel> {

    }

}
