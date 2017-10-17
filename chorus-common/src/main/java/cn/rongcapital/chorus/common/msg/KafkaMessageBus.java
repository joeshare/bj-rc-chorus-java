package cn.rongcapital.chorus.common.msg;

import cn.rongcapital.chorus.common.msg.handler.TopicMessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;

@Component
@Scope("prototype")
@Slf4j
public class KafkaMessageBus extends AbstractMessageBus implements InitializingBean {

    private KafkaConsumer<String, String> consumer;
    @Value("${kafka.consume.group.id}")
    private String groupId;
    @Value("${kafka.server.addresses}")
    private String serverAddress;

    @Override
    public void consume(List<String> topics) {
        if (topics == null || topics.isEmpty()) {
            log.error("No topic given can not consume anything.");
            return;
        }
        Executors.newFixedThreadPool(1).execute(() -> {
            try {
                consumer.subscribe(topics);
                while (true) {
                    ConsumerRecords<String, String> records = consumer.poll(100L);
                    topics.stream().forEach(topic -> {
                        records.records(topic).forEach(record -> {
                            String curTopic = record.topic();
                            log.debug("Consume topic [{}] message [{}].", curTopic, record.value());
                            TopicMessageHandler handler = getHandler(curTopic);
                            if (handler == null) {
                                log.warn("No handler found for topic [{}], skip the message.", curTopic);
                                return;
                            }
                            handler.handleMessage(record.value());
                        });
                    });
                }
            } finally {
                consumer.close();
            }
        });
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Properties props = new Properties();
        props.put("bootstrap.servers", serverAddress);
        props.put("group.id", groupId);
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());
        consumer = new KafkaConsumer<>(props);
    }
}
