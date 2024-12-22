package net.n.example.high_concurrent_java_server.draft.web.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketPushService {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketPushService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // Send a message to a topic
    public void sendMessageToTopic(String topic, Object message) {
        messagingTemplate.convertAndSend(topic, message);
    }

    // Send a message to a specific user (requires authentication setup)
    public void sendMessageToUser(String user, String destination, Object message) {
        messagingTemplate.convertAndSendToUser(user, destination, message);
    }
}
