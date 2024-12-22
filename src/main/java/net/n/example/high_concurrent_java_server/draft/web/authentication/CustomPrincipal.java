package net.n.example.high_concurrent_java_server.draft.web.authentication;

import java.security.Principal;

public class CustomPrincipal implements Principal {

    private final String name;
    private final int userId;

    public CustomPrincipal(String name, int userId) {
        this.name = name;
        this.userId = userId;
    }

    @Override
    public String getName() {
        return name;
    }

    public int getUserId() {
        return userId;
    }
}
