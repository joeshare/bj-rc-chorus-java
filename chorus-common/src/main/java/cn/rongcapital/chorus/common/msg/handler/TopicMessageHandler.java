package cn.rongcapital.chorus.common.msg.handler;

public interface TopicMessageHandler<T> {


    void handleMessage(T content);

}
