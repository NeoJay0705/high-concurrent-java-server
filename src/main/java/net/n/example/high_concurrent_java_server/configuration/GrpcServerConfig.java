package net.n.example.high_concurrent_java_server.configuration;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptor;
import net.n.example.high_concurrent_java_server.draft.gRPC.serivce.GreetingServiceImpl;

@Configuration
public class GrpcServerConfig {

    @Bean
    public Server grpcServer(GreetingServiceImpl greetingService,
            ServerInterceptor headerInterceptor) {
        // Custom virtual thread executor
        Executor virtualThreadExecutor = command -> Thread.ofVirtual().start(command);

        return ServerBuilder.forPort(50051).addService(greetingService) // Add your gRPC service
                                                                        // implementation
                .intercept(headerInterceptor) // Add interceptors
                .executor(virtualThreadExecutor) // Use virtual threads
                // .sslContext(GrpcSslContexts.forServer(certChainFile, privateKeyFile).build())
                // .maxConnectionIdle(5, TimeUnit.MINUTES) // Close idle connections after 5
                // minutes, Default: No idle timeout.
                // .maxConnectionAge(30, TimeUnit.MINUTES) // Close connections after 30 minutes,
                // Default: No age limit.
                // .maxConnectionAgeGrace(5, TimeUnit.MINUTES) // Allow 5 minutes for ongoing RPCs
                // .maxInboundMessageSize(50 * 1024 * 1024) // 50 MiB
                // .maxInboundMetadataSize(8192) // 8 KiB
                .build();
    }
}
