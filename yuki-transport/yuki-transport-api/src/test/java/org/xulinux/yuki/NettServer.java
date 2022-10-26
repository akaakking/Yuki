package org.xulinux.yuki;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;


/**
 * //TODO add class commment here
 *
 * @Author wfh
 * @Date 2022/10/14 下午2:43
 */
public class NettServer {
    public static void main(String[] args) {
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();

        final NettyServerHandler nettyServerHandler = new NettyServerHandler();
        final Encoder encoder = new Encoder();

        serverBootstrap.group(boss,worker)
                .channel(NioServerSocketChannel.class)
                .localAddress(9140)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new ChunkedWriteHandler())
                                .addLast(encoder)
                                .addLast(nettyServerHandler);
                    }
                });
        try {
            ChannelFuture sync = serverBootstrap.bind().sync();
            sync.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
