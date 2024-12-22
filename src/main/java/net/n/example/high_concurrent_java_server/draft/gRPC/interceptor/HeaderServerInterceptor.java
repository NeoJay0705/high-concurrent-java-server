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

        @Override
        public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call,
                        Metadata headers, ServerCallHandler<ReqT, RespT> next) {

                // Extract metadata
                Metadata.Key<String> userAgentKey =
                                Metadata.Key.of("user-agent", Metadata.ASCII_STRING_MARSHALLER);
                Metadata.Key<String> customKey =
                                Metadata.Key.of("custom-header", Metadata.ASCII_STRING_MARSHALLER);

                String userAgent = headers.get(userAgentKey);
                String customHeaderValue = headers.get(customKey);

                // System.out.println("Received User-Agent: " + userAgent);
                // System.out.println("Received Custom Header: " + customHeaderValue);

                // // Proceed with the call
                // return next.startCall(call, headers);
                // System.out.println("Current Context in service: " + Context.current());

                Context context = Context.current().withValue(Context.key("custom-header"),
                                customHeaderValue);

                // Pass the context along the call chain
                // TODO
                return Contexts.interceptCall(context, call, headers, next);
        }
}
