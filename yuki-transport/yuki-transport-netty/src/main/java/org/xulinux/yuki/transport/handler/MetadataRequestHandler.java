package org.xulinux.yuki.transport.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import org.xulinux.yuki.common.fileUtil.ResourceMetadata;
import org.xulinux.yuki.transport.Message;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * //TODO add class commment here
 *
 * @Author wfh
 * @Date 2022/10/26 上午10:28
 */
public class MetadataRequestHandler extends SimpleChannelInboundHandler<Message> {
    private ConcurrentHashMap<String,String> id2path;

    // todo
    private ConcurrentHashMap<String,ResourceMetadata> resourceCache;

    /**
     * 应对 metadataRequestHandler
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        if (msg.getSectionInfos() != null) {
            ctx.fireChannelRead(msg);
            return;
        }

        String resourceId = msg.getResourceId();

        //
        String path = id2path.get(resourceId);

        // 好的系统设计处处是缓存，比如说咱们这里就可以做一个，todo
        // 缓存就是在要生成东西的地方做个map先去get一下
        ResourceMetadata  resourceMetadata = new ResourceMetadata(path);

        Message response = new Message();
        response.setType(Message.Type.METADATA_RESPONSE);
        response.setMetadata(resourceMetadata);

        ctx.writeAndFlush(response);
    }

    public void setId2path(ConcurrentHashMap<String, String> id2path) {
        this.id2path = id2path;
    }
}
