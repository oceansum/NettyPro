package com.ocean.netty.source.echo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import java.security.cert.CertificateException;

/**
 * Echoes back any received data from a client
 */
public class EchoServer {

    static final boolean SSL = System.getProperty("ssl") !=null;
    static final int PORT = Integer.parseInt(System.getProperty("port","8007"));

    public static void main(String[] args) throws Exception {
        //Configure SSL
        final SslContext sslCtx;
        if (SSL){
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forServer(ssc.certificate(),ssc.privateKey()).build();
        }else {
            sslCtx = null;
        }

        //Configure the server
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            if (sslCtx != null){
                                pipeline.addLast(sslCtx.newHandler(ch.alloc()));
                            }
                            pipeline.addLast(new LoggingHandler(LogLevel.INFO));
//                            pipeline.addLast(new EchoServerHandler());
                        }
                    });

            // Start the server.
            ChannelFuture channelFuture = serverBootstrap.bind(PORT).sync();

            // Wait until the socket is closed.
            channelFuture.channel().closeFuture().sync();
        }finally {
            //Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
