package cn.rongcapital.chorus.monitor.springxd;

import cn.rongcapital.chorus.common.constant.StatusCode;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.apache.http.protocol.HTTP;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by shicheng on 2017/4/12.
 */
@Slf4j
@Component
public class TicketController {

    @Value(value = "${notify.ticket.api.url}")
    private String ticketApiUrl;

    @Value(value = "${notify.ticket.agent.id}")
    private String agentId;

    @Value(value = "${notify.ticket.queue.by}")
    private String queuedBy;

    @Value(value = "${notify.ticket.service.key}")
    private String serviceKey;

    @Value(value = "${notify.ticket.hostname}")
    private String hostname;

    /**
     * 创建 Ticket
     *
     * @return 状态: true | false
     */
    public void createTicket() {
        Ticket ticket = new Ticket();
        Ticket.Agent agent = ticket.new Agent();
        agent.setAgent_id(agentId);
        agent.setQueued_at(new Date().toString());
        agent.setQueued_by(queuedBy);
        ticket.setAgent(agent);
        Ticket.Detail detail = ticket.new Detail();
        detail.setHostname(hostname);
        detail.setStatus("problem");
        detail.setPriority(3);
        ticket.setDetails(detail);
        ticket.setEvent_type("trigger");
        ticket.setService_key(serviceKey);
        ticket.setDescription(StatusCode.EXECUTION_UNIT_PARTIAL_STARTED.getDesc());
        try {
            Response execute = Request.Post(ticketApiUrl)
                    .addHeader(HTTP.CONTENT_TYPE, "application/json")
                    .bodyString(JSON.toJSONString(ticket), ContentType.APPLICATION_JSON).execute();
        } catch (Exception e) {
            log.error("send ticket error", e);
        }
    }

}
