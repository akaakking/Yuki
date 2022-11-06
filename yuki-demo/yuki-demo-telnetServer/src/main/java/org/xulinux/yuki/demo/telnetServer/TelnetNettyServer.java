package org.xulinux.yuki.demo.telnetServer;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.xulinux.yuki.nodeServer.NodeServer;

/**
 * //TODO add class commment here
 *
 * @Author wfh
 * @Date 2022/10/29 下午9:04
 */
public class TelnetNettyServer {
    private NodeServer nodeServer;

    public TelnetNettyServer(NodeServer nodeServer) {
        this.nodeServer = nodeServer;
    }

    public void start(int port) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(1);

        ServerBootstrap bootstrap = new ServerBootstrap();

        bootstrap.group(bossGroup,workerGroup)
                .channel(NioServerSocketChannel.class)
                .localAddress(port)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new LineBasedFrameDecoder(1024))
                                .addLast(new StringEncoder())
                                .addLast(new StringDecoder())
                                .addLast(new TelnetHandler(nodeServer));
                    }
                });

        try {
            bootstrap.bind().sync().channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
