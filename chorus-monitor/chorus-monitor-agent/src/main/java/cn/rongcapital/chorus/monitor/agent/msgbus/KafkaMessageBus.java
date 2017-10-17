package cn.rongcapital.chorus.monitor.agent.msgbus;

import cn.rongcapital.chorus.monitor.agent.util.ConfigCenter;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

public class KafkaMessageBus<V> implements MessageBus<JsonJMSMessage, Future<RecordMetadata>> {

    private KafkaProducer kafkaProducer;

    public KafkaMessageBus() {
        Map<String, Object> config = new HashMap<>();
        config.put("bootstrap.servers", ConfigCenter.getBrokers());
        config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        config.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProducer = new KafkaProducer<String, String>(config);
    }


    @Override
    public Future<RecordMetadata> send(JsonJMSMessage jsonJMSMessage) {
        return kafkaProducer.send(new ProducerRecord(jsonJMSMessage.getTopic(), jsonJMSMessage.getJson()));
    }
}
