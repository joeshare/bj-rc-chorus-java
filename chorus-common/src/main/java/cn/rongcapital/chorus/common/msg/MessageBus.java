package cn.rongcapital.chorus.common.msg;

import cn.rongcapital.chorus.common.msg.handler.TopicMessageHandler;

import java.util.List;

/**
 * @author li.hzh
 */
public interface MessageBus {

    void consume(List<String> topics);

    void registTopicHandler(String topic, TopicMessageHandler handler);
}
