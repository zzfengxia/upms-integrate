package com.zz.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2023-04-14 10:37
 * @desc 自定义netty服务端消息处理器，泛型为接收消息对象
 * ************************************
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<String> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("接收到消息：" + msg);
        ctx.writeAndFlush("你好，客户端！我已收到你的消息：" + msg);
    }
}
