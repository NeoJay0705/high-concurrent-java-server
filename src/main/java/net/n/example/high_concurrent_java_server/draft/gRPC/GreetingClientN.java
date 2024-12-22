package net.n.example.high_concurrent_java_server.draft.gRPC;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import net.n.example.high_concurrent_java_server.draft.gRPC.proto.GreetingProto.HelloReply;
import net.n.example.high_concurrent_java_server.draft.gRPC.proto.GreetingProto.HelloRequest;
import net.n.example.high_concurrent_java_server.draft.gRPC.proto.GreetingServiceGrpc;

public class GreetingClientN {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        // Number of requests to send
        int N = 2;
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                // .maxInboundMessageSize(50 * 1024 * 1024)
                .usePlaintext() // Disable
                // SSL
                // for
                // simplicity
                .build();
        // Create a stub (client)
        GreetingServiceGrpc.GreetingServiceFutureStub futureStub =
                GreetingServiceGrpc.newFutureStub(channel);

        // Set the properties programmatically
        System.setProperty("jdk.virtualThreadScheduler.parallelism", "1");
        System.setProperty("jdk.virtualThreadScheduler.maxPoolSize", "1");
        System.setProperty("jdk.virtualThreadScheduler.minRunnable", "1");
        // Run N requests using virtual threads
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            List<StructuredTaskScope.Subtask<HelloReply>> subtasks = new ArrayList<>();

            for (int i = 0; i < N; i++) {
                int requestId = i; // Capture the loop variable for thread-safe use
                // can request lots of requests at the same time
                // if (i % 70000 == 0) {
                // Thread.sleep(1000);
                // System.out.println(i);
                // }

                subtasks.add(scope.fork(() -> {
                    // Prepare request
                    HelloRequest request =
                            HelloRequest.newBuilder().setName("Request " + requestId).build();

                    System.out.println("is virtual thread : " + Thread.currentThread().isVirtual());

                    synchronized (Object.class) {
                        // sleep
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    // Send request and receive response
                    // *** this .get doesn't pin the thread ***
                    HelloReply helloReply = futureStub.sayHello(request).get();
                    System.out.println("Response from server: " + helloReply.getMessage());
                    return helloReply;
                }));
            }

            // Wait for all tasks to complete
            scope.join();
            scope.throwIfFailed();

            // Process results
            for (StructuredTaskScope.Subtask<HelloReply> subtask : subtasks) {
                HelloReply reply = subtask.get();
                // System.out.println("Response from server: " + reply.getMessage());
            }
        }


        // Shutdown channel
        channel.shutdown();
    }
}
