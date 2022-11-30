package org.xulinux.yuki.transport.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.commons.io.FileUtils;
import org.xulinux.yuki.common.JobMetaData;
import org.xulinux.yuki.common.ProgressBar;
import org.xulinux.yuki.common.Speaker;
import org.xulinux.yuki.common.fileUtil.FileUtil;
import org.xulinux.yuki.common.recorder.FileReceiveRecorder;
import org.xulinux.yuki.common.recorder.ResourcePathRecorder;
import org.xulinux.yuki.common.NodeInfo;
import org.xulinux.yuki.transport.Message;
import org.xulinux.yuki.transport.TransportClient;
import org.xulinux.yuki.transport.handler.ClientDecoder;
import org.xulinux.yuki.transport.handler.Encoder;
import org.xulinux.yuki.transport.handler.MetadataResponseHandler;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * mutiClient
 *
 * @Author wfh
 * @Date 2022/10/11 下午4:33
 */
public class NettyTransportClient implements TransportClient {
    private EventLoopGroup eventLoopGroup;
    private volatile Bootstrap bootstrap;

    private ProgressBar progressBar;

    private BlockingQueue<FileReceiveRecorder> waitingJobs;

    public NettyTransportClient() {
        waitingJobs = new LinkedBlockingQueue<>();
    }

    @Override
    public void download(List<NodeInfo> recipients, JobMetaData jobMetaData, ProgressBar progressBar) {
        this.progressBar = progressBar;
        initBootstrap(recipients,jobMetaData);

        NodeInfo nodeInfo = recipients.get(0);

        try {
            ChannelFuture connect = bootstrap.connect(nodeInfo.getIp(), nodeInfo.getPort()).sync();

            // 请求 metadata
            Message message = new Message();
            message.setType(Message.Type.METADATA_REQUEST);
            message.setResourceId(jobMetaData.getResourceId());

            connect.channel().writeAndFlush(message);

            progressBar.show();
            // TODO 全部结束关闭netty 等等等。。。。。
            close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    @Override
    public void resumeTransmission(List<NodeInfo> resourceHoders, File[] logs) {
        initBootstrap(resourceHoders.size());

        // 开始封装
        FileReceiveRecorder[] recorders = new FileReceiveRecorder[logs.length];
        for (int i = 0; i < recorders.length; i++) {
            recorders[i] = new FileReceiveRecorder(logs[i].getPath());
            ChannelFuture connect = bootstrap.connect();

            final int finalI = i;
            connect.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    future.channel().pipeline().get(ClientDecoder.class).sendJob(recorders[finalI]);
                }
            });
        }

        // logs  >= resourcehoders
        for (int i = resourceHoders.size(); i < recorders.length; i++) {
            waitingJobs.add(recorders[i]);
        }
    }

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

    private void initBootstrap(int resouceHolderCount) {
        eventLoopGroup = new NioEventLoopGroup(resouceHolderCount);
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup);
        bootstrap.channel(NioSocketChannel.class);
        final Encoder encoder = new Encoder();

        bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel channel) throws Exception {
                ClientDecoder clientDecoder = new ClientDecoder();
                clientDecoder.setWaitingJobs(waitingJobs);

                channel.pipeline().addLast(encoder);
                channel.pipeline().addLast(clientDecoder);
            }
        });
    }

    private void initBootstrap(List<NodeInfo> nodeInfos, JobMetaData jobMetaData) {
        eventLoopGroup = new NioEventLoopGroup(nodeInfos.size());
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup);
        bootstrap.channel(NioSocketChannel.class);

        final MetadataResponseHandler metadataResponseHandler = new MetadataResponseHandler();
        metadataResponseHandler.setBootstrap(bootstrap);
        metadataResponseHandler.setNodeInfos(nodeInfos);
        metadataResponseHandler.setJobMetaData(jobMetaData);
        metadataResponseHandler.setProgressBar(progressBar);
        final Encoder encoder = new Encoder();

        // 内部类 final ？
        bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel channel) throws Exception {
                ClientDecoder clientDecoder = new ClientDecoder();
                clientDecoder.setWaitingJobs(waitingJobs);

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
