package org.xulinux.yuki.transport.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.xulinux.yuki.transport.TransportServer;
import org.xulinux.yuki.transport.handler.Encoder;
import org.xulinux.yuki.transport.handler.FileTransferHandler;
import org.xulinux.yuki.transport.handler.MetadataRequestHandler;
import org.xulinux.yuki.transport.handler.ServerDecoder;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @Author wfh
 * @Date 2022/10/11 下午2:53
 */
public class NettyTransportServer implements TransportServer {
    public static final int DEFAULT_SERVER_PORT = 9140;
    private int port;

    private ConcurrentHashMap<String,String> id2path;
    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workGroup;
    private AtomicInteger transCount = new AtomicInteger(0);

    @Override
    public void start(ConcurrentHashMap<String,String> id2path) {
        this.id2path = id2path;
        if (bossGroup != null) {
            return;
        }

        if (port == 0) {
            this.port = DEFAULT_SERVER_PORT;
        }

        bossGroup = new NioEventLoopGroup(1);
        workGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();

        serverBootstrap.group(bossGroup,workGroup)
                .channel(NioServerSocketChannel.class)
                .localAddress(port)
                .option(ChannelOption.SO_KEEPALIVE,true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        // can  share ？
                        FileTransferHandler fileTransferHandler = new FileTransferHandler(transCount);
                        fileTransferHandler.setId2path(id2path);
                        MetadataRequestHandler metadataRequestHandler = new MetadataRequestHandler();
                        metadataRequestHandler.setId2path(id2path);

                        socketChannel.pipeline().addLast(new ChunkedWriteHandler())
                                .addLast(new Encoder())
                                .addLast(new ServerDecoder())
                                .addLast(metadataRequestHandler)
                                .addLast(fileTransferHandler);
                    }
                });

        serverBootstrap.bind(port);
    }
      @Override
    public void terminal() {
        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
    }

    @Override
    public AtomicInteger transporting() {
        return transCount;
    }

    @Override
    public void setPort(int port) {
        this.port = port;
    }
}
