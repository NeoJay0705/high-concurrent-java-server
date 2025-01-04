package net.n.example.high_concurrent_java_server.draft.gRPC.interceptor;

import java.util.Random;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.ForwardingClientCallListener;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.Status;

public class HeaderClientInterceptor implements ClientInterceptor {

    private final Random random = new Random();

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method,
            io.grpc.CallOptions callOptions, io.grpc.Channel next) {

        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(
                next.newCall(method, callOptions)) {

            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                // Add custom headers
                Metadata.Key<String> customKey =
                        Metadata.Key.of("custom-header", Metadata.ASCII_STRING_MARSHALLER);
                Metadata.Key<String> tokenKey =
                        Metadata.Key.of("authorization-token", Metadata.ASCII_STRING_MARSHALLER);

                headers.put(customKey, random.nextInt(1000) + "");
                headers.put(tokenKey, "Bearer my-token");

                System.out.println("Added headers: " + headers);

                super.start(new ForwardingClientCallListener.SimpleForwardingClientCallListener<>(
                        responseListener) {
                    @Override
                    public void onHeaders(Metadata headers) {
                        System.out.println("Received headers: " + headers);
                        super.onHeaders(headers);
                    }

                    @Override
                    public void onClose(Status status, Metadata trailers) {
                        System.out.println("Call closed with status: " + status);
                        super.onClose(status, trailers);
                    }
                }, headers);
            }
        };
    }
}
