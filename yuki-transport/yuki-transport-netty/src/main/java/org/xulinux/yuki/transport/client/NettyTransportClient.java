package org.xulinux.yuki.transport.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.commons.io.FileUtils;
import org.xulinux.yuki.common.fileUtil.FileUtil;
import org.xulinux.yuki.common.recorder.ResourcePathRecorder;
import org.xulinux.yuki.registry.NodeInfo;
import org.xulinux.yuki.transport.Message;
import org.xulinux.yuki.transport.TransportClient;
import org.xulinux.yuki.transport.handler.ClientDecoder;
import org.xulinux.yuki.transport.handler.Encoder;
import org.xulinux.yuki.transport.handler.MetadataResponseHandler;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

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
    private CountDownLatch countDownLatch;
    private String resourceId;
    private String downDir;

    public NettyTransportClient() {
    }

    @Override
    public void download(String resourceId, List<NodeInfo> recipients, String downDir) {
        this.downDir = downDir;
        nodeInfos = recipients;
        this.resourceId = resourceId;
        int recipientsCount = recipients.size();

        this.countDownLatch = new CountDownLatch(recipientsCount);

        ininBootstrap(recipientsCount);

        NodeInfo nodeInfo = recipients.get(0);

        try {
            ChannelFuture connect = bootstrap.connect(nodeInfo.getIp(), nodeInfo.getPort()).sync();

            // 请求 metadata
            Message message = new Message();
            message.setType(Message.Type.METADATA_REQUEST);
            message.setResourceId(resourceId);

            connect.channel().writeAndFlush(message);

            countDownLatch.await();
            rmLogs(resourceId);
            // TODO 全部结束关闭netty 等等等。。。。。
            close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    private void rmLogs(String resourceId) {
        String aofDirPath = ResourcePathRecorder.getAofDirPath();

        File file = new File(aofDirPath);

        File[] files = file.listFiles(f -> f.getName().startsWith(resourceId));

        for (File f : files) {
            f.delete();
        }
    }

    /**
     * 开启续传,两种情况提前设计
     */
    @Override
    public void resumeTransmission() {

    }

    /**
     * 删除续传log
     */
    @Override
    public void rmLogAndResource() {
        File aofDir = new File(ResourcePathRecorder.getAofDirPath());

        File[] files = aofDir.listFiles(f -> !f.getName().equals(ResourcePathRecorder.id2pathFileName));

        String resourceId = files[0].getName();

        String baseDir = FileUtil.readList(files[0]).get(0);

        try {
            FileUtils.deleteDirectory(new File(baseDir + "/" + resourceId));

            for (File f : files) {
                FileUtils.deleteDirectory(f);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 客户端的bootstrap
     * @param serverNum
     */
    private void ininBootstrap(int serverNum) {
        MetadataResponseHandler metadataResponseHandler = new MetadataResponseHandler();
        metadataResponseHandler.setBootstrap(bootstrap);
        metadataResponseHandler.setNodeInfos(nodeInfos);
        metadataResponseHandler.setResourceId(resourceId);
        metadataResponseHandler.setDownDir(downDir);
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
                clientDecoder.setCountDownLatch(countDownLatch);
                clientDecoder.setBaseDir(downDir);

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
