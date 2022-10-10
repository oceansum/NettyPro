package com.ocean.netty.dubborpc.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.lang.reflect.Proxy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NettyClient {

    //创建线程池
    private static ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private static NettyClientHandler client;

    private int count = 0;

    //编写方法使用代理模式，获取一个代理对象
    public Object getBean(final Class<?> serviceClass, final String providerName){
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class<?>[]{serviceClass},(proxy, method, args) -> {
                    System.out.println("(proxy, method, args) 进入...." + (++count) + " 次");
                    //{} 部分的代码，客户端每调用一次 hello，就会进入到该代码
                    if (client == null){
                        initClient();
                    }

                    //设置要发给服务端的消息
                    //providerName 协议头 args[0] 就是客户端调用api hello(???), 参数
                    client.setPara(providerName + args[0]);

                    return executor.submit(client).get();
                });
    }
    //初始化客户端
    private static void initClient(){
        System.out.println("开始初始化");
        client = new NettyClientHandler();
        System.out.println("1");
        //创建EventLoopGroup
        NioEventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY,true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new StringDecoder());
                        pipeline.addLast(new StringEncoder());
                        pipeline.addLast(client);
                    }
                });
        System.out.println("2");
        try{
            System.out.println("3");
//            bootstrap.connect("127.0.0.1", 7000).sync();
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 7000).sync();
            System.out.println("4");
//            channelFuture.channel().closeFuture().sync();
            System.out.println("客户端初始化完成");
        }catch (Exception e){
            e.printStackTrace();
        }
//        finally {
//            System.out.println("5");
//            group.shutdownGracefully();
//        }
    }
}
