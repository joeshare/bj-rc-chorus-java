package cn.rongcapital.chorus.monitor.agent.msgbus;

public interface MessageBus<I, O> {

    O send(I input);
}
