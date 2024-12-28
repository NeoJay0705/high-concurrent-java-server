package net.n.example.high_concurrent_java_server.draft.web.websocket;

import java.security.Principal;
import java.util.Map;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import net.n.example.high_concurrent_java_server.draft.web.security.identity.PrincipleManager;
import net.n.example.high_concurrent_java_server.draft.web.security.identity.UserSecurity;

public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    private final PrincipleManager principleManager;

    public CustomHandshakeHandler(PrincipleManager principleManager) {
        this.principleManager = principleManager;
    }

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
            Map<String, Object> attributes) {

        Principal principle =
                principleManager.getPrinciple((String) attributes.get(UserSecurity.TOKEN));

        return new StompPrincipal(principle.getName());
    }
}
