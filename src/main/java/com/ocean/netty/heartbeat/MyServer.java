package com.ocean.netty.heartbeat;

import com.ocean.netty.groupchat.GroupChatServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class MyServer {
    public static void main(String[] args) {

        //创建两个线程组
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)//使用NioServerSocketChannel作为服务器的通道实现
                    .handler(new LoggingHandler(LogLevel.INFO))  //在bossGroup 增加一个日志处理器
                    .childHandler(new ChannelInitializer<SocketChannel>() { //给 workerGroup 的 EventLoop 对应的管道设置处理器
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //获取到pipeline
                            ChannelPipeline pipeline = ch.pipeline();
                            /**
                             * 加入一个netty 提供 IdleStateHandler
                             *说明
                             *  1.IdleStateHandler 是 netty 提供的处理空闲状态的处理器
                             *  2.long readerIdleTime ：表示多长时间没有读，就会发送一个心跳检测包检测是否连接
                             *  3.long writerIdleTime ：表示多长时间没有xie，就会发送一个心跳检测包检测是否连接
                             *  4.long allIdleTime ：表示多长时间没有读写，就会发送一个心跳检测包检测是否连接
                             *
                             *  5.文档说明
                             *      Triggers an IdleStateEvent when a Channel has not performed read, write, or both operation for a while.
                             *  6.当 IdleStateHandler 触发后，就会传递给管道的下一个 handler 去处理
                             *      通过调用（触发）下一个 handler 的  ，在该方法中去处理 IdleStateHandler(读空闲，写空闲，读写空闲)
                             */
                            pipeline.addLast(new IdleStateHandler(3,5,7, TimeUnit.SECONDS));
                            //加入一个对空闲监测进一步处理的handler（自定义）
                            pipeline.addLast(new MyServerHandler());
                        }
                    });
            ChannelFuture channelFuture = serverBootstrap.bind(7000).sync();
            channelFuture.channel().closeFuture().sync();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
