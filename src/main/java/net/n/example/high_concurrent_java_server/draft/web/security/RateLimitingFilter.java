package net.n.example.high_concurrent_java_server.draft.web.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.n.example.high_concurrent_java_server.draft.web.authentication.CustomPrincipal;

// @Component
public class RateLimitingFilter implements Filter {

    // map[endpoint:method]
    private final Map<String, RateLimiter> rateLimiters;
    private final RateLimiter defaultRateLimiter;

    public RateLimitingFilter() {
        // Initialize the rate limiter with a bucket capacity of 10 tokens and a refill rate of 2
        // tokens per second
        this.rateLimiters = getRateLimiter();
        this.defaultRateLimiter = new RateLimiter(10, 10);
    }

    private Map<String, RateLimiter> getRateLimiter() {
        Map<String, RateLimiter> rateLimiters = new HashMap<>();
        rateLimiters.put(keyEncode("/test/authorize", "GET"), new RateLimiter(3, 5));
        return rateLimiters;
    }

    private static String keyEncode(String endpoint, String method) {
        return endpoint + ":" + method;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String endpoint = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();

        String id;
        Optional<CustomPrincipal> principle =
                Optional.ofNullable(((CustomPrincipal) httpRequest.getUserPrincipal()));

        if (principle.isPresent()) {
            id = String.valueOf(principle.get().getUserId());
        } else {
            endpoint = "common";
            method = "common";
            id = httpRequest.getRemoteAddr();
        }

        RateLimiter rateLimiter =
                rateLimiters.getOrDefault(keyEncode(endpoint, method), defaultRateLimiter);
        if (!rateLimiter.allowRequest(id)) {
            httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            // filter is out of process of GlobalExceptionHandler
            httpResponse.getWriter().write("{\"code\":\"429\", \"data\":\"Too Many Requests\"}");
            return;
        }

        chain.doFilter(request, response);
    }

    public static class TooManyRequestsException extends RuntimeException {
        public TooManyRequestsException(String message) {
            super(message);
        }
    }
}
