package net.n.example.high_concurrent_java_server.draft.gRPC.interceptor;

import org.springframework.stereotype.Component;
import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;

@Component
public class HeaderServerInterceptor implements ServerInterceptor {

        public static final Context.Key<Metadata> METADATA_KEY = Context.key("metadata");

        public static final Metadata.Key<String> CUSTOM_KEY =
                        Metadata.Key.of("custom-header", Metadata.ASCII_STRING_MARSHALLER);

        @Override
        public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call,
                        Metadata headers, ServerCallHandler<ReqT, RespT> next) {

                // Extract metadata
                String customHeaderValue = headers.get(CUSTOM_KEY);
                System.out.println("Received Custom Header: " + customHeaderValue);

                Context ctx = Context.current() // from thread local -> Node -> METADATA_KEY, value
                                .withValue(METADATA_KEY, headers);

                // Pass the context along the call chain
                return Contexts.interceptCall(ctx, call, headers, next);
        }
}
