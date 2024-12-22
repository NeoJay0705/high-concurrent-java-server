package net.n.example.high_concurrent_java_server.configuration;

import org.springframework.stereotype.Component;
import io.grpc.Server;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GrpcServerLifecycleManager {

    private final Server grpcServer;

    public GrpcServerLifecycleManager(Server grpcServer) {
        this.grpcServer = grpcServer;
    }

    @PostConstruct
    public void startGrpcServer() throws Exception {
        log.info("Starting gRPC server...");
        grpcServer.start();
        log.info("gRPC server started on port " + grpcServer.getPort());
    }

    @PreDestroy
    public void stopGrpcServer() throws Exception {
        log.info("Stopping gRPC server...");
        grpcServer.shutdown();
        log.info("gRPC server stopped.");
    }
}
