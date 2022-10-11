package org.xulinux.yuki.transport;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.HashMap;

/**
 * //TODO add class commment here
 *
 * @Author wfh
 * @Date 2022/10/11 下午2:53
 */
public class NettyServer implements TransportServer{
    private int port = 9140;
    NioEventLoopGroup bossGroup;
    NioEventLoopGroup workGroup;

    @Override
    public void start() {
        bossGroup = new NioEventLoopGroup(1);
        workGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();

        serverBootstrap.group(bossGroup,workGroup)
                .channel(NioServerSocketChannel.class) // 每个channel都有一个pipeline
                .localAddress(port)
                .option(ChannelOption.SO_KEEPALIVE,true)
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {

                    }
                });

        try {
            ChannelFuture channelFuture= serverBootstrap.bind().sync();

            ChannelFuture closeFuture = channelFuture.channel().closeFuture();
            closeFuture.sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    @Override
    public void terminal() {
        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
    }

}
