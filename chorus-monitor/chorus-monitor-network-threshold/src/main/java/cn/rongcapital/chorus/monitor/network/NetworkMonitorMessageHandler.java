package cn.rongcapital.chorus.monitor.network;

import cn.rongcapital.chorus.common.msg.handler.TopicMessageHandler;
import cn.rongcapital.chorus.common.util.JsonUtils;
import cn.rongcapital.chorus.common.util.SpringBeanUtils;
import cn.rongcapital.chorus.das.entity.process.ProcessIOMonitorModel;
import cn.rongcapital.chorus.das.service.ProjectMemberMailSenderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class NetworkMonitorMessageHandler implements TopicMessageHandler<String> {

    private static final String MAIL_TEMPLATE_NAME = "network-io-notify.ftl";
    @Autowired
    private ProjectMemberMailSenderService mailSenderService;

    @Override
    public void handleMessage(String content) {
        if (content == null || content.isEmpty()) {
            log.warn("Message content is empty.");
            return;
        }
        try {
            List<ProcessIOMonitorModel> monitorModels = JsonUtils.array2List(content, ProcessIOMonitorModel.class);
            monitorModels.stream().forEach(monitorModel -> {
                Map<String, Object> paramMap = generateParamMap(monitorModel);
                mailSenderService.sendMail(monitorModel.getProjectCode(), MAIL_TEMPLATE_NAME, "数据导出流量预警", paramMap);
            });
        } catch (Exception e) {
            log.error("Json to object error!!", e);
            log.error("JSON [{}].", content);
            return;
        }
    }

    private Map<String, Object> generateParamMap(ProcessIOMonitorModel monitorModel) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("evtTime", monitorModel.getTime());
        paramMap.put("user", monitorModel.getUser());
        paramMap.put("projectCode", monitorModel.getProjectCode());
        paramMap.put("cmdLine", monitorModel.getCmdLine());
        paramMap.put("output", monitorModel.getOutputVolume());
        paramMap.put("hostname", monitorModel.getHostname());
        return paramMap;
    }

}
