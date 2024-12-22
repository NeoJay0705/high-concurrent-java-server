package net.n.example.high_concurrent_java_server.draft.web.authentication;

import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebFilter("/*") // Apply this filter to all routes
public class TokenFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization logic if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // Cast to HttpServletRequest and HttpServletResponse
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Extract the Authorization header
        String token = httpRequest.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            try {
                // Extract and validate the token
                String authToken = token.substring(7); // Remove "Bearer " prefix
                String username = validateToken(authToken); // Implement your token validation logic

                if (username != null) {
                    // Wrap the request with a custom Principal
                    CustomPrincipal customPrincipal = new CustomPrincipal(username, 1);
                    httpRequest = new CustomHttpServletRequestWrapper(httpRequest, customPrincipal);
                }
            } catch (Exception ex) {
                // Handle invalid token
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        // Continue the filter chain
        chain.doFilter(httpRequest, httpResponse);
    }

    @Override
    public void destroy() {
        // Cleanup logic if needed
    }

    // Dummy validation method (replace with actual validation logic)
    private String validateToken(String token) {
        // Decode and validate the token, then extract the username or user ID
        return "test001".equals(token) ? "test001" : null; // Replace with actual logic to extract
                                                           // user details
    }
}
