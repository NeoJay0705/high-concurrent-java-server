package net.n.example.high_concurrent_java_server.draft.gRPC.serivce;

import org.springframework.stereotype.Service;
import io.grpc.Metadata;
import io.grpc.stub.StreamObserver;
import net.n.example.high_concurrent_java_server.draft.gRPC.interceptor.HeaderServerInterceptor;
import net.n.example.high_concurrent_java_server.draft.gRPC.proto.GreetingProto.HelloReply;
import net.n.example.high_concurrent_java_server.draft.gRPC.proto.GreetingProto.HelloRequest;
import net.n.example.high_concurrent_java_server.draft.gRPC.proto.GreetingServiceGrpc;

@Service
public class GreetingServiceImpl extends GreetingServiceGrpc.GreetingServiceImplBase {
    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        // Retrieve metadata from the Context
        Metadata metadata = HeaderServerInterceptor.METADATA_KEY.get();
        String myHeaderValue = metadata.get(HeaderServerInterceptor.CUSTOM_KEY);

        System.out.println("Custom Header in service: " + myHeaderValue);

        String message = "Hello, " + request.getName();
        HelloReply reply = HelloReply.newBuilder().setMessage(message).build();

        // Send response
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}
