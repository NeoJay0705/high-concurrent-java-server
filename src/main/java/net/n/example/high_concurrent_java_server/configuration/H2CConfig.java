package net.n.example.high_concurrent_java_server.configuration;

import org.apache.coyote.http2.Http2Protocol;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class H2CConfig {

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatH2CWebServerCustomizer() {
        return factory -> factory.addConnectorCustomizers(connector -> {
            connector.setScheme("http");
            connector.setSecure(false);
            connector.setPort(8080); // HTTP port
            connector.addUpgradeProtocol(new Http2Protocol()); // Enable H2C
        });
    }
}
