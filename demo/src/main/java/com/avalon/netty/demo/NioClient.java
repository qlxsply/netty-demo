package com.avalon.netty.demo;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NioClient {

    public static void main(String[] args) throws Exception {
        SocketChannel socketChannel = SocketChannel.open();

        socketChannel.configureBlocking(false);

        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 9000);

        if (!socketChannel.connect(inetSocketAddress)) {
            System.out.println("当前操纵是非阻塞模式，正在建立连接......");
            while (!socketChannel.finishConnect()) {
                System.out.println("因为连接需要时间，客户端不会阻塞，可以做其它工作");
            }
        }

        System.out.println("连接已经成功......");
        ByteBuffer wrap = ByteBuffer.wrap("hell 1234567890".getBytes());

        //发送数据
        socketChannel.write(wrap);

        //
        System.in.read();

    }

}
