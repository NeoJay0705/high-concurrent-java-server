package net.n.example.high_concurrent_java_server.draft.web.service;

import java.util.List;
import java.util.Random;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import net.n.example.high_concurrent_java_server.draft.web.service.KafkaDeadLetterService.DeadLetterMessage;
import net.n.example.high_concurrent_java_server.draft.web.service.ServerSendEvent.SSMessage;

@Service
public class KafkaConsumerService {

    private final SimpMessagingTemplate messagingTemplate;
    private final KafkaDeadLetterService kafkaDeadLetterService;

    private Random random = new Random();

    public KafkaConsumerService(SimpMessagingTemplate messagingTemplate,
            KafkaDeadLetterService kafkaDeadLetterService) {
        this.kafkaDeadLetterService = kafkaDeadLetterService;
        this.messagingTemplate = messagingTemplate;
    }

    // dispatch listener concurrency commit error dead-letter
    // Listen for messages from Kafka and send them to the correct WebSocket session
    @KafkaListener(topics = {"topic_chatroom"}, containerFactory = "batchConsumerContainerFactory")
    public void listen(List<ConsumerRecord<String, SSMessage>> records, Acknowledgment ack,
            Consumer<?, ?> consumer) {

        int nextInt = random.nextInt(100);
        for (ConsumerRecord<String, SSMessage> record : records) {
            try {
                if (true) {
                    throw new RuntimeException("test");
                }
                System.out.println(Thread.currentThread().getName() + nextInt
                        + " - Processing record: " + record.value());
            } catch (Exception e) {
                kafkaDeadLetterService.sendMessage(new DeadLetterMessage(e, record));
            }
        }
        ack.acknowledge();
    }

}

