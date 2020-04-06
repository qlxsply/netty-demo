package com.avalon.netty.chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Date;
import java.util.Iterator;

public class ChatServer {

    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private static final int PORT = 13000;

    public ChatServer() {
        try {
            //创建选择器
            this.selector = Selector.open();
            //创建serverSocketChannel
            this.serverSocketChannel = ServerSocketChannel.open();
            //绑定指定端口
            this.serverSocketChannel.bind(new InetSocketAddress(PORT));
            //设置非阻塞模式
            this.serverSocketChannel.configureBlocking(false);
            //绑定至selector，监听socket连接事件
            this.serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void listen() {
        try {
            //循环处理
            while (true) {
                int select = selector.select(1000);
                if (select > 0) {
                    //遍历得到selectionKey集合
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    //逐个处理每一个事件
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();

                        if (key.isAcceptable()) {
                            String name = Thread.currentThread().getName();
                            System.out.println("我是线程：" + name + "，收到一个创建连接事件");
                            //为每一个连接请求创建一个socket管道
                            SocketChannel sc = serverSocketChannel.accept();
                            //设置为非阻塞模式
                            sc.configureBlocking(false);
                            //注册socket至selector，监听读事件
                            sc.register(selector, SelectionKey.OP_READ);
                            //
                            System.out.println("当前连接远程IP：" + sc.getRemoteAddress().toString().substring(1));
                        }

                        if (key.isReadable()) {
                            //当前事件是读事件，处于可读状态
                            read(key);
                        }

                        //处理完成，移除事件
                        iterator.remove();
                    }
                    //事件处理完成
                    continue;
                }
                System.out.println("没有事件发生，继续等待，time > " + new Date());
            }
        } catch (Exception e) {
            System.out.println("捕获了监听异常");
            e.printStackTrace();
        }
    }

    private void read(SelectionKey key) {
        //获取当前事件对应的socket管道
        SocketChannel channel = (SocketChannel) key.channel();
        //缓存
        ByteBuffer byteBuffer = ByteBuffer.allocate(2048);
        //获取来源地址
        String address = "";
        try {
            address = channel.getRemoteAddress().toString().substring(1);
        } catch (IOException e) {
            System.out.println("获取管道地址异常！");
            e.printStackTrace();
        }
        //
        int read = 0;
        try {
            read = channel.read(byteBuffer);
        } catch (IOException e) {
            System.out.println(address + " 客户下线！");
            try {
                channel.close();
            } catch (IOException ex) {
                System.out.println("捕获管道关闭异常");
                ex.printStackTrace();
            }
        }

        if (read > 0) {
            //获取缓冲区数据
            String msg = new String(byteBuffer.array());
            //
            System.out.println("当前地址：" + address + "，当前读取消息：" + msg.trim());
            //转发
            this.send(msg, channel);
        }
    }

    private void send(String msg, SocketChannel self) {
        System.out.println("我是线程：" + Thread.currentThread().getName() + "，准备发送消息！");
        //遍历当前selector上的全部绑定事件
        for (SelectionKey key : selector.keys()) {
            //通过key获取对应的channel
            Channel channel = key.channel();
            //判断当前管道类型并不是自身
            if (channel instanceof SocketChannel && channel != self) {
                //转型
                SocketChannel socketChannel = (SocketChannel) channel;
                //将msg转储到buffer
                ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
                //将buffer的数据写入通道
                try {
                    socketChannel.write(buffer);
                } catch (IOException e) {
                    System.out.println("捕获了发送异常");
                    e.printStackTrace();
                    try {
                        channel.close();
                    } catch (IOException ex) {
                        System.out.println("捕获了发送关闭异常");
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        ChatServer chatServer = new ChatServer();
        chatServer.listen();
    }

}
