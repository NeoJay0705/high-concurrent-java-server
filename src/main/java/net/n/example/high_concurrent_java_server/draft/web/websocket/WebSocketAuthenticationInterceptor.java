package net.n.example.high_concurrent_java_server.draft.web.websocket;

import java.util.Map;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
import net.n.example.high_concurrent_java_server.draft.web.security.identity.UserSecurity;
import net.n.example.high_concurrent_java_server.util.HttpUtil;

public class WebSocketAuthenticationInterceptor extends HttpSessionHandshakeInterceptor {
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
            WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        Map<String, String> queryParams = HttpUtil.getQueryParam(request.getURI().getQuery());
        if (queryParams.containsKey(UserSecurity.TOKEN)) {
            String token = queryParams.get(UserSecurity.TOKEN);
            attributes.put(UserSecurity.TOKEN, token);
        }
        return super.beforeHandshake(request, response, wsHandler, attributes);
    }
}
