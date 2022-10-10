package com.ocean.netty.protocoltcp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class MyClientHandler extends SimpleChannelInboundHandler<MessageProtocol> {
    private int count;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProtocol msg) throws Exception {
        int len = msg.getLen();
        byte[] content = msg.getContent();

        System.out.println("客户端接收到消息如下：");
        System.out.println("长度="  + len);
        System.out.println("内容=" + new String(content, StandardCharsets.UTF_8));
        System.out.println("客户端接收消息数量 =" + (++this.count));

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        //使用客户端发送10条数据
        for (int i = 0; i < 10; i++){
            String msg = "oceanX";
            byte[] content = msg.getBytes(StandardCharsets.UTF_8);
            int length = msg.getBytes(StandardCharsets.UTF_8).length;

            //创建协议包对象
            MessageProtocol messageProtocol = new MessageProtocol();
            messageProtocol.setLen(length);
            messageProtocol.setContent(content);
            ctx.writeAndFlush(messageProtocol);

        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("异常信息=" + cause.getMessage());
        ctx.close();
    }
}
