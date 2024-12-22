package net.n.example.high_concurrent_java_server.draft.web.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import lombok.Data;

@Component
public class ServerSendEvent {
    private final WebSocketPushService pushService;

    public ServerSendEvent(WebSocketPushService pushService) {
        this.pushService = pushService;
    }

    // Send a message to all clients every 5 seconds
    @Scheduled(fixedRate = 1000)
    public void sendPeriodicUpdates() {
        SSMessage message = new SSMessage();
        message.setContent("Server update at " + System.currentTimeMillis());
        message.setTopic("/topic/chatroom1");
        pushService.sendMessageToTopic("/topic/chatroom1", message);
    }

    @Scheduled(fixedRate = 1000)
    public void sendPeriodicUpdates2() {
        SSMessage message = new SSMessage();
        message.setContent("Server update at " + System.currentTimeMillis());
        message.setTopic("/topic/chatroom2");
        pushService.sendMessageToTopic("/topic/chatroom2", message);
    }

    @Data
    public static class SSMessage {
        private String topic;
        private String content;
    }
}
