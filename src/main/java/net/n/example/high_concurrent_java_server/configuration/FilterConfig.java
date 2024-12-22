package net.n.example.high_concurrent_java_server.configuration;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import net.n.example.high_concurrent_java_server.draft.web.authentication.TokenFilter;
import net.n.example.high_concurrent_java_server.draft.web.security.RateLimitingFilter;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<TokenFilter> tokenFilterRegistry() {
        FilterRegistrationBean<TokenFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new TokenFilter());
        registrationBean.addUrlPatterns("/*"); // Apply to all URLs
        registrationBean.setOrder(1); // Ensure it's executed early
        return registrationBean;
    }

    // @Bean
    public FilterRegistrationBean<RateLimitingFilter> rateLimitingFilterRegistry() {
        FilterRegistrationBean<RateLimitingFilter> registrationBean =
                new FilterRegistrationBean<>();
        registrationBean.setFilter(new RateLimitingFilter());
        registrationBean.addUrlPatterns("/*"); // Apply rate limiting to specific paths
        registrationBean.setOrder(2); // Ensure it runs early in the chain
        return registrationBean;
    }
}
