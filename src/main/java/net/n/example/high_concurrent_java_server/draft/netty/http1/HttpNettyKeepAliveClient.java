package net.n.example.high_concurrent_java_server.draft.netty.http1;

import java.util.concurrent.CompletableFuture;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import lombok.SneakyThrows;

public class HttpNettyKeepAliveClient {
    @SneakyThrows
    public static void main(String[] args) {
        // Target HTTP/2 server
        String host = "localhost";
        int port = 8080;
        String uri = "/test/lag1";

        // Event loop group
        EventLoopGroup group = new NioEventLoopGroup();
        // Create Bootstrap
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioSocketChannel.class).handler(new HttpChannelHandler());

        // Connect to the server
        Channel channel = bootstrap.connect(host, port).sync().channel();
        sendRequest(1, host, port, "/test/lag1", channel);
        channel.pipeline().get(HttpRequestHandler.class).resetFuture();
        sendRequest(2, host, port, "/test/lag2", channel);

        group.shutdownGracefully();
    }

    @SneakyThrows
    public static void sendRequest(int i, String host, int port, String uri, Channel channel) {
        // Create the HTTP GET request
        FullHttpRequest request =
                new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri);
        // Set the request headers
        request.headers().set(HttpHeaderNames.HOST, host);
        // request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE); // Close
        // after
        // request
        request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);
        request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);

        // Send the request
        channel.writeAndFlush(request);

        // Wait for the response
        // System.out.println(i + " -> Waiting for response...");

        CompletableFuture<FullHttpResponse> responseFuture =
                channel.pipeline().get(HttpRequestHandler.class).getFuture();
        // Wait for the response
        FullHttpResponse response = responseFuture.join();

        // Process the response
        System.out.println(i + " -> object " + response.hashCode());
        System.out.println(i + " -> Final Response Status: " + response.status());
        System.out.println(
                i + " -> Final Response Body: " + response.content().toString(CharsetUtil.UTF_8));

        // Release the response
        response.release();
    }
}
