package org.xulinux.yuki.transport.handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import org.xulinux.yuki.common.JobMetaData;
import org.xulinux.yuki.common.ProgressBar;
import org.xulinux.yuki.common.fileUtil.FileSectionInfo;
import org.xulinux.yuki.common.fileUtil.ResourceMetadata;
import org.xulinux.yuki.common.recorder.FileReceiveRecorder;
import org.xulinux.yuki.common.NodeInfo;
import org.xulinux.yuki.transport.Message;

import java.util.List;

/**
 * //TODO add class commment here
 *
 * @Author wfh
 * @Date 2022/10/26 上午10:30
 */
@ChannelHandler.Sharable
public class MetadataResponseHandler extends SimpleChannelInboundHandler<ResourceMetadata> {
    private List<NodeInfo> nodeInfos;
    private Bootstrap bootstrap;

    private JobMetaData jobMetaData;

    private ProgressBar progressBar;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ResourceMetadata resourceMetadata) throws Exception {
        resourceMetadata.creatDir(jobMetaData.getDownDir());
        jobMetaData.setResourceDirName(resourceMetadata.getResourceDirName());

        // connect and request
        List<List<FileSectionInfo>> sections = resourceMetadata.split(nodeInfos.size());
        sendJob(ctx.channel(), jobMetaData, nodeInfos.get(0), sections.get(0));

        for (int i = 1; i < nodeInfos.size(); i++) {
            NodeInfo nodeInfo = nodeInfos.get(i);
            ChannelFuture connect = bootstrap.connect(nodeInfo.getIp(), nodeInfo.getPort());
            final List<FileSectionInfo> fileSectionInfos = sections.get(i);
            final int nodeNum = i;

            connect.addListener((ChannelFutureListener) channelFuture ->
                    sendJob(channelFuture.channel(),jobMetaData, nodeInfos.get(nodeNum),fileSectionInfos));
        }
    }

    public void sendJob(Channel ch, JobMetaData protoJob, NodeInfo nodeInfo, List<FileSectionInfo> fileSectionInfos) {
        JobMetaData jobMetaData = protoJob.clone(fileSectionInfos, nodeInfo.getHostString());
        FileReceiveRecorder recorder = new FileReceiveRecorder(jobMetaData);
        this.progressBar.add(recorder.getTotalSize());
        ch.pipeline().get(ClientDecoder.class).sendJob(recorder);
    }

    public void setJobMetaData(JobMetaData jobMetaData) {
        this.jobMetaData = jobMetaData;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public void setNodeInfos(List<NodeInfo> nodeInfos) {
        this.nodeInfos = nodeInfos;
    }

    public void setBootstrap(Bootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

}
