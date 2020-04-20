package com.avalon.netty.simple;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer {

    public static void main(String[] args) {
        //创建 bossGroup 和 workerGroup
        //1.创建两个线程组 bossGroup 和 workerGroup
        //2.boss只负责处理连接请求，对客户端的业务处理会交给workerGroup
        //3.两者都是无限循环
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            //创建服务器的启动对象，配置参数
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            //设置服务器参数
            serverBootstrap.group(bossGroup, workerGroup) //设置线程组合
                    .channel(NioServerSocketChannel.class)//使用 NioServerSocketChannel 作为服务器通道实现
                    .option(ChannelOption.SO_BACKLOG, 4)//设置线程队列连接的个数
                    .childOption(ChannelOption.SO_KEEPALIVE, true)//设置保持连接状态
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            System.out.println("initChannel");
                            ch.pipeline().addLast(new NettyServerHandler());
                        }
                    });//为workerGroup的eventLoop对应的管道设置处理器

            System.out.println("服务器启动完成......");

            try {
                //绑定一个端口并且同步，生成了一个ChannelFuture对象
                ChannelFuture channelFuture = serverBootstrap.bind(8888).sync();
                //对关闭通道进行监听
                channelFuture.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
