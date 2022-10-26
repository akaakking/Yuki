package org.xulinux.yuki.transport.handler;

import com.alibaba.fastjson.JSON;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import org.xulinux.yuki.common.fileUtil.FileSectionInfo;
import org.xulinux.yuki.common.fileUtil.ResourceMetadata;
import org.xulinux.yuki.registry.NodeInfo;
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
    // 还有一个需要考虑的点就是遍历的时候，我这个是arraylist所以下标遍历起来很快的，如果是linklist会很慢
    private List<NodeInfo> nodeInfos;
    private Bootstrap bootstrap;
    private String resourceId;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ResourceMetadata resourceMetadata) throws Exception {
        // todo create dir

        // connect and request
        resourceMetadata.setHolderCount(nodeInfos.size());
        List<List<FileSectionInfo>> sections = resourceMetadata.split();

        sendJob(ctx.channel(),sections.get(0));

        for (int i = 1; i < nodeInfos.size(); i++) {
            NodeInfo nodeInfo = nodeInfos.get(i);
            ChannelFuture connect = bootstrap.connect(nodeInfo.getIp(), nodeInfo.getPort());
            final List<FileSectionInfo> fileSectionInfos = sections.get(i);

            connect.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    sendJob(channelFuture.channel(),fileSectionInfos);
                }
            });
        }
    }

    private void sendJob(Channel ch, List<FileSectionInfo> fileSectionInfos) {
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
