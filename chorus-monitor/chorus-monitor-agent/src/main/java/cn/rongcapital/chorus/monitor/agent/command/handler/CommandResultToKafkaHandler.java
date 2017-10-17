package cn.rongcapital.chorus.monitor.agent.command.handler;

import cn.rongcapital.chorus.monitor.agent.command.parser.CommandOutputParser;
import cn.rongcapital.chorus.monitor.agent.command.result.CommandResult;
import cn.rongcapital.chorus.monitor.agent.msgbus.JsonJMSMessage;
import cn.rongcapital.chorus.monitor.agent.msgbus.KafkaMessageBus;
import cn.rongcapital.chorus.monitor.agent.util.JsonUtil;

/**
 * Covert content to json string, then send it to kafka.
 *
 * @param <T>
 */
public class CommandResultToKafkaHandler<T> implements CommandResultHandler<T> {

    private CommandOutputParser parser;
    private KafkaMessageBus kafkaMessageBus = new KafkaMessageBus();
    private String topic;

    public CommandResultToKafkaHandler(CommandOutputParser parser, String topic) {
        this.parser = parser;
        this.topic = topic;
    }

    @Override
    public void handle(CommandResult<T> result) {
        JsonJMSMessage message = generateMessage(result);
        kafkaMessageBus.send(message);
    }

    @Override
    public CommandOutputParser getParser() {
        return parser;
    }

    private JsonJMSMessage generateMessage(CommandResult result) {
        String json = JsonUtil.toJson(result.getContent());
        return new JsonJMSMessage(topic, json);
    }
}
