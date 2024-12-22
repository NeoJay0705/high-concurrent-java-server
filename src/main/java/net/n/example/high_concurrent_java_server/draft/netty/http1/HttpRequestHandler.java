package net.n.example.high_concurrent_java_server.draft.netty.http1;

import java.util.concurrent.CompletableFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;

public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpResponse> {

    private CompletableFuture<FullHttpResponse> responseFuture;

    public HttpRequestHandler() {
        this.responseFuture = new CompletableFuture<>();
    }

    public CompletableFuture<FullHttpResponse> getFuture() {
        return responseFuture;
    }

    public void resetFuture() {
        this.responseFuture = new CompletableFuture<>();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse response)
            throws Exception {
        try {
            // Complete the CompletableFuture
            responseFuture.complete(response.retain());

            // Decide to close the connection based on the response headers
            if (HttpHeaderValues.CLOSE
                    .contentEqualsIgnoreCase(response.headers().get(HttpHeaderNames.CONNECTION))) {
                ctx.close(); // Close if Connection: close
            }

            Thread.sleep(1000);
            System.out.println(" [IO Task] Done");
        } catch (Exception e) {
            responseFuture.completeExceptionally(e);
        } finally {
            // Do not close ctx here for persistent connections (if reused)
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // Complete the CompletableFuture exceptionally in case of errors
        responseFuture.completeExceptionally(cause);
        cause.printStackTrace();
        ctx.close();
    }
}
