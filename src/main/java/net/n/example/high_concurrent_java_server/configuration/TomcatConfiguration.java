package net.n.example.high_concurrent_java_server.configuration;

import java.lang.Thread.Builder.OfVirtual;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfiguration {

    @Bean
    public TomcatProtocolHandlerCustomizer<?> protocolHandlerVirtualThreadExecutorCustomizer() {
        return protocolHandler -> {
            OfVirtual ofVirtual = Thread.ofVirtual().name("virtual-tomcat#", 1);
            ThreadFactory factory = ofVirtual.factory();
            protocolHandler.setExecutor(Executors.newThreadPerTaskExecutor(factory));
        };
    }
}
