package com.ocean.netty.codec;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

/**
 * 说明
 * 1、我们自定义一个Handler 需要继承netty规定好的某个HandlerAdapter（规范）
 * 2、这是我们自定义一个Handler，才能称为一个Handler
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    //读取数据实际（这里我们可以读取客户端发送的消息）
    /**
     * 1、ChannelHandlerContext ctx：上下文对象，含有 管道pipeline,通道channel,地址
     * Object msg :就是客户端发送的数据 默认Object
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{

        //读取从客户端发送的StudentPojo.Student
        StudentPOJO.Student student = (StudentPOJO.Student) msg;
        System.out.println("  id=" + student.getId() + " 名字=" + student.getName());
    }
    //数据读取完毕
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception{

        //writeAndFlush 是 write + flush
        //将数据写入到缓存，并刷新
        //一般讲，我们对这个发送的数据进行编码
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello,客户端~1",CharsetUtil.UTF_8));
    }

    //处理异常，一般是需要关闭通道

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause) throws Exception{
        ctx.close();
    }
}
