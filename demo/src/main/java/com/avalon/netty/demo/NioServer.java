package com.avalon.netty.demo;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NioServer {

    public static void main(String[] args) throws Exception {
        //1.创建server socket channel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //2.
        Selector selector = Selector.open();
        //3.
        serverSocketChannel.socket().bind(new InetSocketAddress(9000));
        //4.设置为非阻塞模式
        serverSocketChannel.configureBlocking(false);
        //.注册至selector
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            if (selector.select(1000) == 0) {
                System.out.println("服务器等待一秒，没有连接");
                continue;
            }
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                if (key.isAcceptable()) {
                    //建立连接，生成一个socketChannel
                    SocketChannel channel = serverSocketChannel.accept();
                    //设置成非阻塞模式
                    channel.configureBlocking(false);
                    //
                    System.out.println("连接创建成功 >> " + channel.hashCode());
                    //
                    channel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(2048));
                    System.out.println("当前事件是连接事件 >>>>  " + channel.getRemoteAddress().toString());
                }

                if (key.isReadable()) {
                    SocketChannel channel = (SocketChannel) key.channel();
                    ByteBuffer byteBuffer = (ByteBuffer) key.attachment();
                    channel.read(byteBuffer);
                    System.out.println("from 客户端读取数据 " + new String(byteBuffer.array()));
                }

                iterator.remove();
            }
        }

    }

}
