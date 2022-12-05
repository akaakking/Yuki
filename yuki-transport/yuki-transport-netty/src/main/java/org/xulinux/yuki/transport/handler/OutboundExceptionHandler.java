package org.xulinux.yuki.transport.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;

/**
 * //TODO add class commment here
 *
 * @Author wfh
 * @Date 2022/12/4 下午5:56
 */
public class OutboundExceptionHandler extends ChannelOutboundHandlerAdapter {
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
