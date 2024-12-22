package net.n.example.high_concurrent_java_server.draft.web.authentication;

import java.security.Principal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

public class CustomHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private final Principal principal;

    public CustomHttpServletRequestWrapper(HttpServletRequest request, CustomPrincipal principal) {
        super(request);
        this.principal = principal;
    }

    @Override
    public Principal getUserPrincipal() {
        return this.principal;
    }
}
