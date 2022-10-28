package org.xulinux.yuki.transport.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.xulinux.yuki.transport.TransportServer;
import org.xulinux.yuki.transport.handler.FileTransferHandler;
import org.xulinux.yuki.transport.handler.MetadataRequestHandler;
import org.xulinux.yuki.transport.handler.ServerDecoder;


/**
 * @Author wfh
 * @Date 2022/10/11 下午2:53
 */
public class NettyTransportServer implements TransportServer {
    private int port = 9140;
    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workGroup;

    @Override
    public void start() {
        if (bossGroup != null) {
            return;
        }

        bossGroup = new NioEventLoopGroup(1);
        workGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();

        serverBootstrap.group(bossGroup,workGroup)
                .channel(NioServerSocketChannel.class)
                .localAddress(port)
                .option(ChannelOption.SO_KEEPALIVE,true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new ChunkedWriteHandler())
                                .addLast(new ServerDecoder())
                                .addLast(new MetadataRequestHandler())
                                .addLast(new FileTransferHandler());
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
