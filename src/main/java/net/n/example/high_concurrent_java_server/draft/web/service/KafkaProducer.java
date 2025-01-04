package net.n.example.high_concurrent_java_server.draft.web.service;

import java.util.Map.Entry;
import java.util.Optional;
import org.apache.kafka.common.Metric;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

    private KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String topic, Object message) {
        kafkaTemplate.send(topic, message);
        // request-latency-avg
        // request-rate
        Optional<? extends Metric> first = kafkaTemplate.metrics().entrySet().stream()
                .filter(m -> m.getKey().name().equals("request-latency-max")).map(Entry::getValue)
                .findFirst();
        if (first.isPresent()) {
            System.out.println(first.get().metricName() + " : " + first.get().metricValue());
        }

        System.out.println("Sent Message: " + message);
    }
}
