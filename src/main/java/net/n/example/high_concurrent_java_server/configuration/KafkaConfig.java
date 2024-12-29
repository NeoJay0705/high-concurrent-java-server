package net.n.example.high_concurrent_java_server.configuration;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties.AckMode;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

@EnableKafka
@Configuration
public class KafkaConfig {

    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        config.put(ProducerConfig.ACKS_CONFIG, "1");
        config.put(ProducerConfig.RETRIES_CONFIG, "3");

        config.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, "120000");
        config.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, "30000");
        config.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "1");

        config.put(ProducerConfig.BATCH_SIZE_CONFIG, "16384"); // 16 KB
        config.put(ProducerConfig.LINGER_MS_CONFIG, "5");
        config.put(ProducerConfig.BUFFER_MEMORY_CONFIG, "67108864");

        config.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, "10000");
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    public ConsumerFactory<String, String> batchConsumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*"); // Trust all packages
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "default-group");

        // Reliability Configurations
        config.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, "300000"); // 5 minutes
        config.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, "3000"); // 3 seconds
        config.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "10000"); // 10 seconds

        // Performance Configurations
        config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1000");
        config.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, "1");
        config.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, "163840"); // 160 KB
        config.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, "500"); // 500 ms

        // Disable Auto Commit for Manual Offset Management
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> batchConsumerContainerFactory(
            KafkaTemplate<String, Object> kafkaTemplate) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        // Set acknowledgment mode to MANUAL to commit manually
        factory.getContainerProperties().setAckMode(AckMode.MANUAL);

        // Enable batch listening if needed
        factory.setBatchListener(true); // Set to true if using batch listeners

        // Set concurrency based on your partition count
        factory.setConcurrency(1);

        factory.setConsumerFactory(batchConsumerFactory());

        return factory;
    }
}
