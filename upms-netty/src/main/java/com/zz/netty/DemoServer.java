package com.zz.netty;

import com.zz.netty.handler.NettyServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2023-04-14 10:25
 * @desc netty服务端
 * ************************************
 */
public class DemoServer {
    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // 接受连接线程池
        EventLoopGroup workerGroup = new NioEventLoopGroup(); // 处理连接线程池
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // Netty注册的ChannelPipeline是按照添加处理器的顺序依次执行的。
                            // 当消息从一个Channel的尾端流向另一个Channel的首端时，
                            // Netty会沿着Pipeline链逐个执行每个处理器的处理方法，
                            // 以便对消息进行编解码、协议转换、业务处理等操作。
                            // 当一个ChannelHandler将消息处理完成后，
                            // 它可以将结果继续传递给Pipeline中的下一个ChannelHandler，也可以选择直接将消息写回到Channel中。
                            // 在Pipeline中，ChannelHandler的传递顺序可以通过ChannelPipeline.addFirst()、
                            // ChannelPipeline.addLast()、ChannelPipeline.remove()等方法进行调整，以便实现不同的业务需求。
                            ChannelPipeline pipeline = ch.pipeline();
                            // 消息转码器
                            pipeline.addLast(new StringDecoder());
                            pipeline.addLast(new StringEncoder());
                            // 自定义消息处理，可以注册多个消息处理器，按照注册顺序执行，比如在处理消息前先做鉴权的处理等。
                            pipeline.addLast(new NettyServerHandler());
                        }
                    });
            // 监听端口
            ChannelFuture channelFuture = serverBootstrap.bind(8090).sync();
            System.out.println("服务端已启动，监听端口：" + channelFuture.channel().localAddress());
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
