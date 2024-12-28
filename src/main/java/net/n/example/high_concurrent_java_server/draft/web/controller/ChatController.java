package net.n.example.high_concurrent_java_server.draft.web.controller;

import java.security.Principal;
import java.util.Map;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import lombok.Data;

@Controller
public class ChatController {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public ChatController(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    // 1. BROADCAST PATTERN: "/app/broadcast"
    @MessageMapping("/broadcast")
    public void broadcast(@Payload ChatMessage chatMessage) {
        simpMessagingTemplate.convertAndSend("/topic/broadcast", chatMessage);
    }

    // 2. REQUEST/RESPONSE PATTERN: "/app/requestResponse"
    // We'll send a direct message back to the user queue.
    @MessageMapping("/requestResponse")
    public void requestResponse(@Payload ChatMessage chatMessage, Principal principal) {
        String username = principal.getName(); // The user's principal name
        simpMessagingTemplate.convertAndSendToUser(username, "/queue/response", new ChatMessage(
                "Server", "Hello " + username + ", you said: " + chatMessage.getContent()));
    }

    // 3. CHATROOM PATTERN: "/app/chatroom/{room}"
    @MessageMapping("/chatroom/{room}")
    public void chatRoom(@DestinationVariable String room, @Payload ChatMessage chatMessage) {
        // Everyone subscribed to "/topic/chatroom/{room}" receives the message
        simpMessagingTemplate.convertAndSend("/topic/chatroom/" + room, chatMessage);
    }

    // 4. SERVER-SIDE SEND PATTERN
    // An example endpoint to demonstrate pushing to a specific user session
    // We might call this from a REST endpoint or from another @MessageMapping.
    @MessageMapping("/serverSideSend")
    public void serverSideSend(@Payload ChatMessage chatMessage,
            @Headers Map<String, Object> headers) {
        String targetUser = chatMessage.getTo();
        simpMessagingTemplate.convertAndSendToUser(targetUser, "/queue/private", new ChatMessage(
                "Server", "Private message for " + targetUser + ": " + chatMessage.getContent()));
    }

    @Data
    public static class ChatMessage {
        private String from;
        private String to; // for private messaging (optional)
        private String room; // for chatroom usage
        private String content;

        public ChatMessage() {}

        public ChatMessage(String from, String content) {
            this.from = from;
            this.content = content;
        }

        // Getters and Setters...
    }
}
