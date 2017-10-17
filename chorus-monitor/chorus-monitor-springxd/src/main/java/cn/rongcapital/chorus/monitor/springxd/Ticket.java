package cn.rongcapital.chorus.monitor.springxd;

import lombok.Data;

/**
 * Created by shicheng on 2017/4/14.
 */
@Data
public class Ticket {

    private Agent agent;
    private String description;
    private Detail details;
    private String event_type = "trigger";
    private String service_key;

    @Data
    class Agent {
        private String agent_id;
        private String queued_at;
        private String queued_by;
    }

    @Data
    class Detail {
        private String event_id;
        private String hostname;
        private String status;
        private Integer priority;
    }

}
