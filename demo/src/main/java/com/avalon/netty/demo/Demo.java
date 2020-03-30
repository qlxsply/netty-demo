package com.avalon.netty.demo;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Demo {

    public static void main(String[] args) throws Exception {
        ExecutorService executorService = Executors.newCachedThreadPool();

        ServerSocket serverSocket = new ServerSocket(9000);

        System.out.println("starting >>>>> ");

        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("link to server ......");
            executorService.execute(() -> {
                //可以和客户端通讯
                fun(socket);
            });
        }
    }

    public static void fun(Socket socket) {
        try {
            System.out.println("execute fun method starting ......");
            byte[] bytes = new byte[2];
            InputStream inputStream = socket.getInputStream();
            System.out.println(" >>>> " + socket.getRemoteSocketAddress().toString());
            int length = 0;
            while ((length = inputStream.read(bytes)) != -1) {
                System.out.println(Thread.currentThread().getName() + " accept > " + new String(bytes));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("execute fun method ending ......");
    }

}
