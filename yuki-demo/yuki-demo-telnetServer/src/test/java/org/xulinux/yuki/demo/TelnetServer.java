package org.xulinux.yuki.demo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.junit.Test;
import org.xulinux.yuki.common.YUKI;
import org.xulinux.yuki.common.spi.ExtensionLoader;
import org.xulinux.yuki.demo.telnetServer.TelnetHandler;
import org.xulinux.yuki.transport.TransportClient;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * //TODO add class commment here
 *
 * @Author wfh
 * @Date 2022/10/30 下午6:03
 */
public class TelnetServer {

    @Test
    public void SPITest() {
        ExtensionLoader.getExtension(TransportClient.class);
    }

//    public static void main(String[] args) {
//        ServerBootstrap serverBootstrap = new ServerBootstrap();
//
//        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
//        EventLoopGroup workerGroup = new NioEventLoopGroup(2);
//
//        serverBootstrap.group(bossGroup, workerGroup)
//                .channel(NioServerSocketChannel.class)
//                .childHandler(new ChannelInitializer<SocketChannel>() {
//                    @Override
//                    protected void initChannel(SocketChannel socketChannel) throws Exception {
//                        socketChannel.pipeline().addLast(new LineBasedFrameDecoder(1024))
//                                .addLast(new StringEncoder())
//                                .addLast(new StringDecoder())
//                                .addLast(new ChannelInboundHandlerAdapter() {
//                                    @Override
//                                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//                                        ctx.writeAndFlush(YUKI.YUKI_LOGO + "\r\n" + TelnetHandler.PROTMT);
//                                    }
//
//                                    @Override
//                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//                                        if (msg.equals("exit")) {
//                                            ctx.channel().close().sync();
//                                        }
//
//                                        ctx.writeAndFlush("response: " + msg + "\r\n");
//                                        ctx.writeAndFlush(TelnetHandler.PROTMT);
//                                        // 这个线程如果阻塞主的话，会怎样不阻塞会怎样？
//                                    }
//                                });
//                    }
//                });
//        try {
//            serverBootstrap.bind(54188).sync().channel().closeFuture().sync();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } finally {
//            bossGroup.shutdownGracefully();
//            workerGroup.shutdownGracefully();
//        }
//
//    }
}
/**
 * 熟练使用redis 和 mysql,这样才能理解他们
 */
