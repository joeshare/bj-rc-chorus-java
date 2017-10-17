package cn.rongcapital.chorus.monitor.agent.msgbus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode
public class JsonJMSMessage {

    private String topic;
    private String json;

}
