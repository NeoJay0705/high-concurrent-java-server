package net.n.example.high_concurrent_java_server.draft.web.security.identity;

import java.security.Principal;

public class ClientPrinciple implements Principal {

    private String id;

    public ClientPrinciple(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return id;
    }

}
