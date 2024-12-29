package net.n.example.high_concurrent_java_server.draft.web.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.record.TimestampType;
import org.springframework.stereotype.Service;
import lombok.Data;

@Service
public class KafkaDeadLetterService {
    // kafka-topics.sh --create --topic dead-letter-topic --bootstrap-server your_kafka_broker:9092
    // --partitions 1 --replication-factor 1
    public static final String DEAD_LETTER_TOPIC = "dead-letter-topic";

    private final KafkaProducer kafkaProducer;

    public KafkaDeadLetterService(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    public void sendMessage(DeadLetterMessage message) {
        kafkaProducer.sendMessage(DEAD_LETTER_TOPIC, message);
    }

    @Data
    public static class DeadLetterMessage {
        private String topic;
        private String key;
        private long timestamp;
        private TimestampType timestampType;
        private Headers headers;
        private Object value;
        private Exception error;

        public DeadLetterMessage(Exception error, ConsumerRecord<String, ?> message) {
            this.topic = message.topic();
            this.key = message.key();
            this.timestamp = message.timestamp();
            this.timestampType = message.timestampType();
            this.headers = message.headers();
            this.value = message.value();
            this.error = error;
        }
    }
}
