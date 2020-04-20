package com.avalon.netty.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * 自定义一个handler 需要继承netty规定好的某个handlerAdapter
 */
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    /**
     * @param ctx 上下文对象，含有管道pipeline，通道channel，地址
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("当前线程：" + Thread.currentThread().getName() + " channelActive 方法 client ctx = " + ctx);
        ctx.writeAndFlush(Unpooled.copiedBuffer("我是客户端，当前通道就绪，触发了我", CharsetUtil.UTF_8));
    }

    /**
     * @param ctx 上下文对象，含有管道pipeline，通道channel，地址
     * @param msg 客户端发送的数据
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("当前线程：" + Thread.currentThread().getName() + " channelRead 方法 client ctx = " + ctx);
        ByteBuf buf = (ByteBuf) msg;
        System.out.println("服务端返回的数据是：" + buf.toString(CharsetUtil.UTF_8));
        System.out.println("服务端地址：" + ctx.channel().remoteAddress());
    }

//    /**
//     * 数据读取完毕
//     *
//     * @param ctx 上下文对象，含有管道pipeline，通道channel，地址
//     * @throws Exception
//     */
//    @Override
//    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        //将数据写入到缓存，并刷新
//        //一般需要对发送的对象进行编码
//        ctx.writeAndFlush(Unpooled.copiedBuffer("当前线程：" + Thread.currentThread().getName() + " 我是客户端，现在数据读取完毕", CharsetUtil.UTF_8));
//    }

    /**
     * 处理异常，一般需要关闭通道
     *
     * @param ctx   上下文对象，含有管道pipeline，通道channel，地址
     * @param cause 异常原因
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("当前线程：" + Thread.currentThread().getName() + " 发生异常，关闭通道");
        ctx.channel();
    }
}
