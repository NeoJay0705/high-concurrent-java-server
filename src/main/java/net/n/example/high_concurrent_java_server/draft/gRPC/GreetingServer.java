package net.n.example.high_concurrent_java_server.draft.gRPC;

import java.io.IOException;
import java.util.concurrent.Executor;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import net.n.example.high_concurrent_java_server.draft.gRPC.interceptor.HeaderServerInterceptor;
import net.n.example.high_concurrent_java_server.draft.gRPC.serivce.GreetingServiceImpl;

public class GreetingServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        Executor virtualThreadExecutor = command -> Thread.ofVirtual().start(command);

        Server server = ServerBuilder.forPort(50051) // Define the server port
                .addService(new GreetingServiceImpl())
                // .maxInboundMessageSize(50 * 1024 * 1024)
                .intercept(new HeaderServerInterceptor()).executor(virtualThreadExecutor).build();

        System.out.println("Starting server...");
        server.start();
        System.out.println("Server started on port 50051");

        // Keep the server running
        server.awaitTermination();
    }
}
