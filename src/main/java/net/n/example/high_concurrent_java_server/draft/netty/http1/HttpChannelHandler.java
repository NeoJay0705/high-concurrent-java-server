package net.n.example.high_concurrent_java_server.draft.netty.http1;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.ssl.ApplicationProtocolConfig;
import io.netty.handler.ssl.ApplicationProtocolNames;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

public class HttpChannelHandler extends ChannelInitializer<Channel> {
    private ChannelInboundHandler inboundHandler;
    private ChannelInboundHandler sslInboundHandler;
    private String host;
    private Integer port;

    public HttpChannelHandler() {
        this(new HttpRequestHandler(), null, null, null);
    }

    public HttpChannelHandler(ChannelInboundHandler inboundHandler) {
        this(inboundHandler, null, null, null);
    }

    public HttpChannelHandler(ChannelInboundHandler inboundHandler,
            ChannelInboundHandler sslInboundHandler, String host, Integer port) {
        this.host = host;
        this.port = port;
        this.inboundHandler = inboundHandler;
        this.sslInboundHandler = sslInboundHandler;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        if (sslInboundHandler != null && host != null && port != 0) {
            SslContext sslContext = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE) // For testing only
                    .applicationProtocolConfig(new ApplicationProtocolConfig(
                            ApplicationProtocolConfig.Protocol.ALPN,
                            ApplicationProtocolConfig.SelectorFailureBehavior.NO_ADVERTISE,
                            ApplicationProtocolConfig.SelectedListenerFailureBehavior.ACCEPT,
                            ApplicationProtocolNames.HTTP_2, ApplicationProtocolNames.HTTP_1_1))
                    .build();
            pipeline.addLast(sslContext.newHandler(ch.alloc(), host, port));
        }

        pipeline.addLast(new HttpClientCodec()); // HTTP encoder/decoder
        pipeline.addLast(new HttpObjectAggregator(1024 * 1024)); // Aggregates HTTP messages
        pipeline.addLast(new HttpRequestHandler()); // Custom handler for the response
    }
}
