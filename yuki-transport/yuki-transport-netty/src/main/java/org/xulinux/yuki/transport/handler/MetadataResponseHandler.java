package org.xulinux.yuki.transport.handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import org.xulinux.yuki.common.fileUtil.FileSectionInfo;
import org.xulinux.yuki.common.fileUtil.ResourceMetadata;
import org.xulinux.yuki.common.recorder.FileReceiveRecorder;
import org.xulinux.yuki.registry.NodeInfo;
import org.xulinux.yuki.transport.Message;

import java.util.List;

/**
 * //TODO add class commment here
 *
 * @Author wfh
 * @Date 2022/10/26 上午10:30
 */
public class MetadataResponseHandler extends SimpleChannelInboundHandler<ResourceMetadata> {
    private List<NodeInfo> nodeInfos;
    private Bootstrap bootstrap;
    private String resourceId;
    private String downDir;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ResourceMetadata resourceMetadata) throws Exception {
        resourceMetadata.creatDir(downDir);

        // connect and request
        List<List<FileSectionInfo>> sections = resourceMetadata.split(nodeInfos.size());

        sendJob(ctx.channel(),sections.get(0),0);

        for (int i = 1; i < nodeInfos.size(); i++) {
            NodeInfo nodeInfo = nodeInfos.get(i);
            ChannelFuture connect = bootstrap.connect(nodeInfo.getIp(), nodeInfo.getPort());
            final List<FileSectionInfo> fileSectionInfos = sections.get(i);
            final int nodeNum = i;

            connect.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    sendJob(channelFuture.channel(),fileSectionInfos,nodeNum);
                }
            });
        }
    }


    public void setDownDir(String downDir) {
        this.downDir = downDir;
    }

    // 将任务发送给对端同时在自己接收这边做了设置
    private void sendJob(Channel ch, List<FileSectionInfo> fileSectionInfos, int nodeNum) {
        ch.pipeline().get(ClientDecoder.class)
                .setFileSectionInfos(fileSectionInfos);
        ch.pipeline().get(ClientDecoder.class)
                .setRecorder(new FileReceiveRecorder(fileSectionInfos,resourceId,nodeNum,downDir));

        Message message = new Message();
        message.setType(Message.Type.FILE_SECTION_ASSIGN);
        message.setResourceId(resourceId);
        message.setSectionInfos(fileSectionInfos);

        ch.writeAndFlush(message);
    }

    public void setNodeInfos(List<NodeInfo> nodeInfos) {
        this.nodeInfos = nodeInfos;
    }

    public void setBootstrap(Bootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }
}
