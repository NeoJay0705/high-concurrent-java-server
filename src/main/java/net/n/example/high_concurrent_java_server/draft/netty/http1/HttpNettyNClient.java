package net.n.example.high_concurrent_java_server.draft.netty.http1;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
import lombok.SneakyThrows;

/**
 * Require rate limit
 */
public class HttpNettyNClient {
    public static void main(String[] args) {
        // Target HTTP/2 server
        String host = "localhost";
        int port = 8080;
        String uri = "/test/lag1";

        // Event loop group
        EventLoopGroup group = new NioEventLoopGroup(2);
        // Create Bootstrap
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioSocketChannel.class).handler(new HttpChannelHandler());

        ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();


        for (int i = 0; i < 70000; i++) {
            // 8/70000
            // java.net.SocketException: Connection reset
            // at
            // java.base/sun.nio.ch.SocketChannelImpl.throwConnectionReset(SocketChannelImpl.java:401)
            // at java.base/sun.nio.ch.SocketChannelImpl.read(SocketChannelImpl.java:434)
            if (i % 100 == 0) {
                if (i % 1000 == 0) {
                    System.out.println(i);
                }
                // thread sleep
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            int finalI = i;
            executorService.submit(() -> {
                sendRequest(finalI, host, port, "/test/lag_long", bootstrap);
            });
        }

        executorService.close();
        group.shutdownGracefully();
    }

    @SneakyThrows
    public static void sendRequest(int i, String host, int port, String uri, Bootstrap bootstrap) {
        // Connect to the server
        Channel channel = bootstrap.connect(host, port).sync().channel();

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
        // System.out.println(i + " -> Final Response Status: " + response.status());
        // System.out.println(
        // i + " -> Final Response Body: " + response.content().toString(CharsetUtil.UTF_8));

        // Release the response
        response.release();
    }
}
