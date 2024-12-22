package net.n.example.high_concurrent_java_server.draft.web.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import lombok.Data;

@Controller
public class WebSocketController {

    @MessageMapping("/message")
    @SendTo("/topic/messages")
    public Message handleMessage(String message) {
        // Process the message (e.g., log, save, or modify)
        Message m = new Message();
        m.setContent(message);
        return m;
    }

    @Data
    public class Message {
        private String content;
    }
}
