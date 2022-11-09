package org.xulinux.yuki.transport.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.xulinux.yuki.common.fileUtil.ResourceMetadata;
import org.xulinux.yuki.registry.NodeInfo;
import org.xulinux.yuki.transport.Message;
import org.xulinux.yuki.transport.TransportClient;
import org.xulinux.yuki.transport.handler.ClientDecoder;
import org.xulinux.yuki.transport.handler.Encoder;
import org.xulinux.yuki.transport.handler.MetadataRequestHandler;
import org.xulinux.yuki.transport.handler.MetadataResponseHandler;

import java.util.List;

/**
 * mutiClient
 *
 * @Author wfh
 * @Date 2022/10/11 下午4:33
 */
public class NettyTransportClient implements TransportClient {
    private EventLoopGroup eventLoopGroup;
    private volatile Bootstrap bootstrap;
    private List<NodeInfo> nodeInfos;
    private String resourceId;

    public NettyTransportClient() {
    }

    /**
     * 断点续传。
     * 先不考虑对面节点假死的情况，也就是不做计时相关的东西。
     * 但是做try catch 相关吧。
     *
     * @param resourceId
     * @param recipients
     */
    @Override
    public void download(String resourceId, List<NodeInfo> recipients /* 接受者*/) {
        nodeInfos = recipients;
        this.resourceId = resourceId;
        int recipientsCount = recipients.size();

        // 客户端这边Nioenventloop是个什么东西啊。
        ininBootstrap(recipientsCount);

        NodeInfo nodeInfo = recipients.get(0);

        try {
            ChannelFuture connect = bootstrap.connect(nodeInfo.getIp(), nodeInfo.getPort()).sync();

            // 请求 metadata
            Message message = new Message();
            message.setType(Message.Type.METADATA_REQUEST);
            message.setResourceId(resourceId);

            connect.channel().writeAndFlush(message);

            connect.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 客户端部分的bootstrap
    private void ininBootstrap(int serverNum) {
        MetadataResponseHandler metadataResponseHandler = new MetadataResponseHandler();
        metadataResponseHandler.setBootstrap(bootstrap);
        metadataResponseHandler.setNodeInfos(nodeInfos);
        metadataResponseHandler.setResourceId(resourceId);
        Encoder encoder = new Encoder();

        eventLoopGroup = new NioEventLoopGroup(serverNum);
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup);
        bootstrap.channel(NioSocketChannel.class);

        // 内部类 final ？
        bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel channel) throws Exception {
                ClientDecoder clientDecoder = new ClientDecoder();
                clientDecoder.setNodeInfos(nodeInfos);

                channel.pipeline().addLast(encoder);
                channel.pipeline().addLast(clientDecoder);
                channel.pipeline().addLast(metadataResponseHandler);
            }
        });
    }

    public void close() {
        eventLoopGroup.shutdownGracefully();
    }
}
