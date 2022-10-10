package com.ocean.netty.buf;

import com.sun.corba.se.impl.legacy.connection.USLPort;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.StandardCharsets;

public class NettyByteBuf02 {
    public static void main(String[] args) {

        //创建ByteBuf
        ByteBuf byteBuf = Unpooled.copiedBuffer("hello,world!南京", StandardCharsets.UTF_8);
        //使用相关的方法
        if (byteBuf.hasArray()){
            byte[] content = byteBuf.array();
            //将 content 转成字符串
            System.out.println(new String(content, StandardCharsets.UTF_8).trim());

            System.out.println("byteBuf = " + byteBuf);

            System.out.println(byteBuf.arrayOffset());// 数组偏移量 0
            System.out.println(byteBuf.readerIndex());// 0
            System.out.println(byteBuf.writerIndex());// 18
            System.out.println(byteBuf.capacity());// 64
            System.out.println(byteBuf.readByte());
            System.out.println(byteBuf.getByte(0));
            System.out.println(byteBuf.readableBytes());// 可读的字节数 18
//            for (int i = 0; i < byteBuf.readableBytes(); i++) {
//                System.out.println((char) byteBuf.getByte(i));
//            }
            //按照某个范围去取
            System.out.println(byteBuf.getCharSequence(0,4,StandardCharsets.UTF_8));
        }
    }
}
