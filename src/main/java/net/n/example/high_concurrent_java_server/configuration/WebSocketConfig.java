package net.n.example.high_concurrent_java_server.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import net.n.example.high_concurrent_java_server.draft.web.security.identity.CopyTokenPrinciple;
import net.n.example.high_concurrent_java_server.draft.web.websocket.CustomHandshakeHandler;
import net.n.example.high_concurrent_java_server.draft.web.websocket.WebSocketAuthenticationInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register a WebSocket endpoint with SockJS fallback
        registry.addEndpoint("/ws").setAllowedOrigins("localhost") // Replace "*" with
                                                                   // allowed origins
                                                                   // for
                // production
                .setHandshakeHandler(new CustomHandshakeHandler(new CopyTokenPrinciple())) // <----
                                                                                           // set
                                                                                           // custom
                                                                                           // handshake
                .addInterceptors(new WebSocketAuthenticationInterceptor()) // <---- add interceptor
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Destination prefixes for the broker (where clients will subscribe)
        config.enableSimpleBroker("/topic", "/queue");

        // Prefix for messages bound for application-level message handling
        config.setApplicationDestinationPrefixes("/app");
    }
}
