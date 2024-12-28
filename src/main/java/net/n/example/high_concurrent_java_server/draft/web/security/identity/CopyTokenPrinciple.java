package net.n.example.high_concurrent_java_server.draft.web.security.identity;

import java.security.Principal;

public class CopyTokenPrinciple implements PrincipleManager {

    @Override
    public Principal getPrinciple(String token) {
        if (token == null) {
            return new ClientPrinciple(UserSecurity.ANONYMOUS_USER);
        }
        return new ClientPrinciple(token);
    }

}
