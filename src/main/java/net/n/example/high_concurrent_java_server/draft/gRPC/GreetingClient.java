package net.n.example.high_concurrent_java_server.draft.gRPC;

import java.util.concurrent.ExecutionException;
import com.google.common.util.concurrent.ListenableFuture;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import net.n.example.high_concurrent_java_server.draft.gRPC.interceptor.HeaderClientInterceptor;
import net.n.example.high_concurrent_java_server.draft.gRPC.proto.GreetingProto.HelloReply;
import net.n.example.high_concurrent_java_server.draft.gRPC.proto.GreetingProto.HelloRequest;
import net.n.example.high_concurrent_java_server.draft.gRPC.proto.GreetingServiceGrpc;

public class GreetingClient {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        // Create a channel to communicate with the server
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .intercept(new HeaderClientInterceptor()).usePlaintext() // Disable
                // SSL
                // for
                // simplicity
                .build();

        // Create a stub (client)
        GreetingServiceGrpc.GreetingServiceFutureStub futureStub =
                GreetingServiceGrpc.newFutureStub(channel);

        // Prepare request
        HelloRequest request = HelloRequest.newBuilder().setName("John Doe").build();

        // Call the gRPC service asynchronously
        ListenableFuture<HelloReply> listenableFuture = futureStub.sayHello(request);

        System.out.println("Waiting");
        // Send request and receive response
        HelloReply reply = listenableFuture.get();

        // Print response
        System.out.println("Response from server: " + reply.getMessage());

        // Shutdown channel
        channel.shutdown();
    }
}
