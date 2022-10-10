package com.ocean.netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class NettyByteBuf01 {
    public static void main(String[] args) {

        //创建一个ByteBuf
        //说明
        //1.创建 对象 ， 该对象包含一个数组arr，是一个byte[10]
        //2.在 netty 的 buffer 中，不需要使用flip 进行反转
        //  底层维护了 readerIndex 和 writerIndex
        //3.通过 readerIndex 和 writerIndex 和 capacity，将 buffer 分成三个区域
        //

        ByteBuf buffer = Unpooled.buffer(10);
        System.out.println("capacity = " + buffer.capacity());
        for (int i = 0; i < buffer.capacity();i++){
            buffer.writeByte(i);
        }
        System.out.println("capacity = " + buffer.capacity());
        //输出
//        for (int i = 0; i < buffer.capacity();i++){
//            System.out.println(buffer.getByte(i));
//        }
        for (int i = 0; i < buffer.capacity();i++){
            System.out.println(buffer.readByte());
        }
    }
}
