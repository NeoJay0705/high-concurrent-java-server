package net.n.example.high_concurrent_java_server.draft.netty.http2;

import java.util.concurrent.CompletableFuture;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http2.Http2DataFrame;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.handler.codec.http2.Http2HeadersFrame;
import io.netty.util.CharsetUtil;

public class Http2StreamHandler extends ChannelInboundHandlerAdapter {
    private Http2Headers responseHeaders;
    private final StringBuilder responseBody = new StringBuilder();
    private final CompletableFuture<HttpResponse> responseFuture;

    public Http2StreamHandler(CompletableFuture<HttpResponse> responseFuture) {
        this.responseFuture = responseFuture;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Http2HeadersFrame) {
            Http2HeadersFrame headersFrame = (Http2HeadersFrame) msg;
            // Store the response headers
            responseHeaders = headersFrame.headers();
            // System.out.println("Received Headers: " + headersFrame.headers());
        } else if (msg instanceof Http2DataFrame) {
            Http2DataFrame dataFrame = (Http2DataFrame) msg;
            // If this is the last frame, close the channel
            if (dataFrame.isEndStream()) {
                HttpResponse response = new HttpResponse(responseHeaders, responseBody.toString());
                responseFuture.complete(response);
                // The streams under the same channel uses the same event loop.
                // or delegating the processes to other threads
                // Thread.sleep(1000);
                System.out.println("End of Response");
                // ctx.channel().close();
            } else {
                // Append the data to the response body
                ByteBuf content = dataFrame.content();
                responseBody.append(content.toString(CharsetUtil.UTF_8));

                // Release the buffer to prevent memory leaks
                content.release();
                // System.out.println("Received Data: " + content.toString(CharsetUtil.UTF_8));

            }
        } else {
            super.channelRead(ctx, msg);
        }
    }

    public static class HttpResponse {
        private final Http2Headers headers;
        private final String body;

        public HttpResponse(Http2Headers headers, String body) {
            this.headers = headers;
            this.body = body;
        }

        public Http2Headers getHeaders() {
            return headers;
        }

        public String getBody() {
            return body;
        }

        @Override
        public String toString() {
            return "HttpResponse{\n" + "headers=" + headers + ", \nbody='" + body + '\'' + '}';
        }
    }
}
