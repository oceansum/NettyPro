package com.ocean.zerocopy;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class NewIOClient {
    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("localhost",7001));
        String filename = "protoc-3.6.1-win32.zip";

        //得到一个文件channel
        FileChannel fileChannel = new FileInputStream(filename).getChannel();
        //准备发送
        long startTime = System.currentTimeMillis();

        //在linux下一个transferTo方法就可以完成传输
        //在windows下  一次调用transferTo 只能发送8m， 就需要分段传输文件，
        //todo 传输时的位置-> 课后思考。。
        //transferTo 底层使用到零拷贝
        long transferCount = fileChannel.transferTo(0,fileChannel.size(),socketChannel);
        System.out.println("发送的总的字节数 = " + transferCount + " 耗时：" + (System.currentTimeMillis() - startTime));
        //关闭
        fileChannel.close();
    }
}
