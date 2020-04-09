package com.avalon.netty.chat;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

public class ChatClient {

    private Selector selector;
    private SocketChannel socketChannel;
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 13000;
    private String userName;

    public ChatClient() {
        try {
            //创建选择器
            this.selector = Selector.open();
            //创建socketChannel，并连接至服务器
            this.socketChannel = SocketChannel.open(new InetSocketAddress(HOST, PORT));
            //设置非阻塞模式
            this.socketChannel.configureBlocking(false);
            //绑定至selector，监听socket连接事件
            this.socketChannel.register(selector, SelectionKey.OP_READ);
            //得到userName
            this.userName = this.socketChannel.getLocalAddress().toString().substring(1);
            //
            System.out.println("客户端：" + userName + " start >>>");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void post(String msg) {
        try {
            System.out.println("我是客户端：" + userName + "，发送消息 > " + msg);
            this.socketChannel.write(ByteBuffer.wrap(msg.getBytes()));
        } catch (Exception e) {
            System.out.println("捕获了消息发送异常");
            e.printStackTrace();
        }
    }

    private void read() {
        int select = 0;
        try {
            select = selector.select();
        } catch (IOException e) {
            System.out.println("当前获取事件异常！");
            e.printStackTrace();
        }
        if (select > 0) {
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                if (key.isReadable()) {
                    SocketChannel channel = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    try {
                        channel.read(buffer);
                    } catch (IOException e) {
                        System.out.println("当前通道获取数据异常！");
                        try {
                            channel.close();
                        } catch (IOException ex) {
                            System.out.println("当前通道关闭异常！");
                            ex.printStackTrace();
                        }
                    }
                    String msg = new String(buffer.array());
                    System.out.println("我是客户端：" + userName + "，收到消息 > " + msg.trim());
                }
                iterator.remove();
            }
        }
    }

    public static void main(String[] args) {
        ChatClient chatClient = new ChatClient();
        Thread thread = new Thread(() -> {
            while (true) {
                chatClient.read();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "t001");
        thread.start();

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String next = scanner.next();
            chatClient.post(next);
        }
    }

}
