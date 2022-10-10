package com.ocean.netty.source.echo;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;


/**
 * Send one message when a connection is open and echoes back any received
 * data to server. Simply put, the echo client initiates the ping-pong
 * traffic between the echo client and server by sending the first message to the server.
 */
public class EchoClient {

    static final boolean SSL = System.getProperty("ssl") != null;
    static final String HOST = System.getProperty("host","127.0.0.1");
    static final int PORT = Integer.parseInt(System.getProperty("port","8007"));
    static final int SIZE = Integer.parseInt(System.getProperty("size","256"));

    public static void main(String[] args) throws Exception {
        //Configure SSL.git
        final SslContext sslCtx;
        if (SSL){
            sslCtx = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        }else {
            sslCtx = null;
        }

        //Configure the client.
        EventLoopGroup group = new NioEventLoopGroup();
        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY,true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            if (sslCtx != null){
                                pipeline.addLast(sslCtx.newHandler(ch.alloc(),HOST,PORT));
                            }
//                            pipeline.addLast(new LoggingHandler(LogLevel.INFO));
                            pipeline.addLast(new EchoClientHandler());
                        }
                    });
            // Start the client.
            ChannelFuture channelFuture = bootstrap.connect(HOST, PORT).sync();
            // Wait until the connection is closed.
            channelFuture.channel().closeFuture().sync();
        }finally {
            group.shutdownGracefully();
        }
    }
}
