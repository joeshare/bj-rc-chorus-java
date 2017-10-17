package cn.rongcapital.chorus.common.msg;

import cn.rongcapital.chorus.common.msg.handler.TopicMessageHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public abstract class AbstractMessageBus implements MessageBus {

    private static final Map<String, TopicMessageHandler> handlerMap = new ConcurrentHashMap();

    @Override
    public void registTopicHandler(String topic, TopicMessageHandler handler) {
        if (handler == null) {
            log.warn("The handle of topic [{}] is null.", topic);
            return;
        }
        log.debug("Regist handler [{}] for topic [{}].", handler.getClass().getName(), topic);
        handlerMap.put(topic, handler);
    }

    protected TopicMessageHandler getHandler(String topic) {
        return handlerMap.get(topic);
    }
}
