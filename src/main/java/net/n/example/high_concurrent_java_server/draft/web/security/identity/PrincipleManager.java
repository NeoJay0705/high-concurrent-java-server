package net.n.example.high_concurrent_java_server.draft.web.security.identity;

import java.security.Principal;

public interface PrincipleManager {
    Principal getPrinciple(String token);
}
