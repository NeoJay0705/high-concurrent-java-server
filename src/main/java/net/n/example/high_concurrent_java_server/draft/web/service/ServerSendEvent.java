package net.n.example.high_concurrent_java_server.draft.web.service;

import java.time.OffsetDateTime;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import lombok.Data;

@Component
public class ServerSendEvent {
    private final WebSocketPushService pushService;
    private final KafkaProducer kafkaProducer;
    private final KafkaConsumerService kafkaConsumerService;

    public ServerSendEvent(WebSocketPushService pushService, KafkaProducer kafkaProducer,
            KafkaConsumerService kafkaConsumerService) {
        this.pushService = pushService;
        this.kafkaProducer = kafkaProducer;
        this.kafkaConsumerService = kafkaConsumerService;
    }

    // Send a message to all clients every 5 seconds
    @Scheduled(fixedRate = 5000)
    public void sendPeriodicUpdates() {
        SSMessage message = new SSMessage();
        message.setContent("Server update at " + OffsetDateTime.now());
        message.setTopic("/topic/chatroom1");
        kafkaProducer.sendMessage("topic_chatroom", message);
        kafkaProducer.sendMessage("topic_chatroom", message);
    }

    // @Scheduled(fixedRate = 5000)
    public void sendPeriodicUpdates2() {
        SSMessage message = new SSMessage();
        message.setContent("Server update at " + System.currentTimeMillis());
        message.setTopic("/topic/chatroom2");
        kafkaProducer.sendMessage("/topic/chatroom2", message);
    }

    @Data
    public static class SSMessage {
        private String topic;
        private String content;
    }
}
