package net.n.example.high_concurrent_java_server.draft.gRPC.serivce;

import org.springframework.stereotype.Service;
import io.grpc.Context;
import io.grpc.stub.StreamObserver;
import net.n.example.high_concurrent_java_server.draft.gRPC.proto.GreetingProto.HelloReply;
import net.n.example.high_concurrent_java_server.draft.gRPC.proto.GreetingProto.HelloRequest;
import net.n.example.high_concurrent_java_server.draft.gRPC.proto.GreetingServiceGrpc;

@Service
public class GreetingServiceImpl extends GreetingServiceGrpc.GreetingServiceImplBase {
    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        Context.current().run(() -> {
            try {
                Thread.sleep(3000);
                System.out.println(System.currentTimeMillis());
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // System.out.println("Current Context in service: " + Context.current());
            // System.out.println("Current Context in service: " + Context.current());
            // Retrieve metadata from the Context
            String customHeader = (String) Context.key("custom-header").get(Context.current());
            // System.out.println("Custom Header in service: " + customHeader);

            String message = "Hello, " + request.getName();
            HelloReply reply = HelloReply.newBuilder().setMessage(message).build();

            // Send response
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        });
        // Create response

    }
}
