package cn.rongcapital.chorus.modules.utils.retry.listener;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by abiton on 15/08/2017.
 */
@Slf4j
@Configuration
@PropertySource(value = "chorus_lineage.properties")
public class LineageStepListener implements StepExecutionListener, InitializingBean, DisposableBean {


    KafkaProducer<String, String> kafkaProducer;
    @Value("${lineage.kafka.brokers}")
    String brokers;
    @Value("${lineage.kafka.atlasTopic:atlasLineageTopic}")
    String atlasTopic;

    @Override
    public void destroy() throws Exception {
        kafkaProducer.close();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, Object> config = new HashMap<>();

        config.put("bootstrap.servers", brokers);
        config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        config.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProducer = new KafkaProducer<String, String>(config);
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        try {
            String stepName = stepExecution.getStepName();
            ExecutionContext executionContext = stepExecution.getExecutionContext();
            Map<String, Object> inputs = null;
            Map<String, Object> outputs = null;
            String projectId = "";
            if (executionContext.containsKey("inputs")) {
                inputs = (Map<String, Object>) executionContext.get("inputs");
            }
            if (executionContext.containsKey("outputs")) {
                outputs = (Map<String, Object>) executionContext.get("outputs");
            }
            if(executionContext.containsKey("projectId")){
                projectId = (String) executionContext.get("projectId");
            }
            if (inputs != null && outputs != null && !inputs.isEmpty() && !outputs.isEmpty()) {
                JobExecution jobExecution = stepExecution.getJobExecution();
                String jobName = jobExecution.getJobInstance().getJobName();
                long instanceId = jobExecution.getJobInstance().getInstanceId();
                JSONObject json = new JSONObject();
                json.put("jobName", jobName);
                json.put("instanceId", instanceId);
                json.put("stepName", stepName);
                json.put("inputs", inputs);
                json.put("outputs", outputs);
                json.put("startTime", "1970-01-01 00:00:00");
                json.put("endTime", "1970-01-01 00:00:00");
                json.put("projectId", projectId);
                kafkaProducer.send(new ProducerRecord<String, String>(atlasTopic, json.toJSONString()));
            }
        } catch (Exception e) {
            //ignore exception
            log.error("send to kafka error", e);
        }
        return null;
    }
}
