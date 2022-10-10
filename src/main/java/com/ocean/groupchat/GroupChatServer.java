package com.ocean.groupchat;

import java.io.IOException;
import java.io.PushbackInputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * todo 需要修改   bug很多
 */
public class GroupChatServer {
    //定义属性
    private Selector selector;
    private ServerSocketChannel listenChannel;
    private static final int PORT = 6667;

    //构造器
    //初始化工作
    public GroupChatServer(){

        try {
            //得到选择器
            selector = Selector.open();
            //ServerSocketChannel
            listenChannel = ServerSocketChannel.open();
            //绑定端口号
            listenChannel.socket().bind(new InetSocketAddress(PORT));
            //设置非阻塞模式
            listenChannel.configureBlocking(false);
            //将该listenChannel注册到selector
            listenChannel.register(selector, SelectionKey.OP_ACCEPT);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //监听
    public void listen(){
        try {
            //循环处理
            while (true){
                int count = selector.select();
                if (count > 0){//有事件处理

                    //遍历得到selectionKey集合
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()){
                        //取出selectionKey
                        SelectionKey key = iterator.next();

                        //监听到accept
                        if (key.isAcceptable()){
                            SocketChannel sc = listenChannel.accept();
                            sc.configureBlocking(false);
                            //将sc注册到selector上
                            sc.register(selector,SelectionKey.OP_READ);

                            //提示
                            System.out.println(sc.getRemoteAddress() + "上线");
                        }
                        if (key.isReadable()){//通道发送read事件，即通道是可读的状态
                            //处理读（专门写方法）
                            readData(key);

                        }
                        //当前的key删除，防止重复处理
                        iterator.remove();
                    }
                }else {
                    System.out.println("等待.....");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //发生异常处理....
        }
    }

    //读取客户端消息
    private void readData(SelectionKey key){
        //取到关联的channel
        SocketChannel channel = null;

        try {
            //得到channel
            channel = (SocketChannel) key.channel();
            //创建buffer
            ByteBuffer buffer = ByteBuffer.allocate(1024);
//            ByteBuffer buffer = (ByteBuffer) key.attachment();
            int count = channel.read(buffer);
            //根据count值做处理
            if (count > 0){
                //把缓存区的数据转化成字符串
                String msg = new String(buffer.array());
                //输出该消息
                System.out.println("from 客户端：" + msg);
                //向其他客户端转发消息（去掉自己），专门写一个方法来处理
                sendInfoOtherClients(msg,channel);
            }
        }catch (IOException e1){
            try{
                System.out.println(channel.getRemoteAddress() + "离线了...");
                //取消注册
                key.cancel();
                //关闭通道
                channel.close();
            }catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }

    //转发消息给其他客户（通道）
    private void sendInfoOtherClients(String msg,SocketChannel self) throws IOException {
        System.out.println("服务器转发消息中...");
        //遍历 所有注册到selector上的SocketChannel，并排除self
        for (SelectionKey key:selector.keys()){
            //通过key 取出对应的Sock Channel
            Channel targetChannel = key.channel();
            //排除自己
            if (targetChannel instanceof SocketChannel && targetChannel != self){
                //转型
                SocketChannel dest = (SocketChannel) targetChannel;
                //将msg存储到buffer
                ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
                //将buffer的数据写入通道
                dest.write(buffer);
            }
        }
    }


    public static void main(String[] args) {
        //创建服务器对象
        GroupChatServer groupChatServer = new GroupChatServer();
        groupChatServer.listen();

    }
}
