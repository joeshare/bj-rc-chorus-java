package cn.rongcapital.chorus.monitor.network;

import cn.rongcapital.chorus.common.msg.KafkaMessageBus;
import cn.rongcapital.chorus.common.msg.MessageBus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import scala.actors.threadpool.Arrays;

@Component
public class NetworkMonitorBootstrap implements InitializingBean {

    private static final String TOPIC = "chorus-network-monitor-topic";
    @Autowired
    private MessageBus messageBus;
    @Autowired
    private NetworkMonitorMessageHandler monitorMessageHandler;

    @Override
    public void afterPropertiesSet() throws Exception {
        messageBus.registTopicHandler(TOPIC, monitorMessageHandler);
        messageBus.consume(Arrays.asList(new String[]{TOPIC}));
    }

}
