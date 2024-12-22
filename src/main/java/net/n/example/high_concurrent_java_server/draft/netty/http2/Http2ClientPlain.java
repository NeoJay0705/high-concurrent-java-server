package net.n.example.high_concurrent_java_server.draft.netty.http2;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpScheme;
import io.netty.handler.codec.http2.DefaultHttp2Headers;
import io.netty.handler.codec.http2.DefaultHttp2HeadersFrame;
import io.netty.handler.codec.http2.Http2FrameCodec;
import io.netty.handler.codec.http2.Http2FrameCodecBuilder;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.handler.codec.http2.Http2MultiplexHandler;
import io.netty.handler.codec.http2.Http2StreamChannel;
import io.netty.handler.codec.http2.Http2StreamChannelBootstrap;
import net.n.example.high_concurrent_java_server.draft.netty.http2.Http2StreamHandler.HttpResponse;

/**
 * Can accept lots of requests at the same time
 */
public class Http2ClientPlain {
    private static final String HOST = "localhost";
    private static final int PORT = 8080;

    public static void main(String[] args) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ChannelPipeline p = ch.pipeline();

                // Use prior knowledge: directly start HTTP/2 (no upgrade)
                Http2FrameCodec frameCodec = Http2FrameCodecBuilder.forClient().build();
                Http2MultiplexHandler multiplexHandler =
                        new Http2MultiplexHandler(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg)
                                    throws Exception {
                                System.err.println(
                                        "Parent handler received unexpected message: " + msg);
                            }
                        });

                // p.addLast(new LoggingHandler(LogLevel.INFO));
                p.addLast(frameCodec);
                p.addLast(multiplexHandler);
            }
        });

        // Connect to the server
        Channel channel = b.connect(HOST, PORT).sync().channel();

        // Set the properties programmatically
        System.setProperty("jdk.virtualThreadScheduler.parallelism", "1");
        System.setProperty("jdk.virtualThreadScheduler.maxPoolSize", "1");
        System.setProperty("jdk.virtualThreadScheduler.minRunnable", "1");
        ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
        for (int i = 0; i < 2; i++) {
            final int finalI = i;
            executorService.submit(() -> {
                try {
                    // Create a new stream by sending an Http2HeadersFrame via a Http2StreamChannel
                    Http2StreamChannelBootstrap streamBootstrap =
                            new Http2StreamChannelBootstrap(channel);
                    Http2StreamChannel streamChannel = streamBootstrap.open().sync().getNow();

                    // Attach the ClientResponseHandler to the child stream channel's pipeline
                    CompletableFuture<HttpResponse> responseFuture = new CompletableFuture<>();
                    streamChannel.pipeline().addLast(new Http2StreamHandler(responseFuture));

                    // Once the stream channel is established, we can send a GET request as
                    // Http2Headers
                    Http2Headers headers = new DefaultHttp2Headers();
                    headers.method(HttpMethod.GET.asciiName());
                    headers.path("/test/lag1");
                    headers.authority(HOST + ":" + PORT);
                    headers.scheme(HttpScheme.HTTP.name());

                    // Write headers frame with endStream = true (no request body)
                    streamChannel.writeAndFlush(new DefaultHttp2HeadersFrame(headers, true));
                    System.out.println(finalI + " Requesting");

                    // Keep the client running until the future is complete
                    HttpResponse response = responseFuture.join();
                    System.out.println(finalI + " Response: " + response.getBody());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        // Wait until channel is closed (or close after receiving response)
        channel.closeFuture().sync();
        group.shutdownGracefully();
    }
}
