package org.xulinux.yuki;

import io.netty.channel.*;

/**
 * //TODO add class commment here
 *
 * @Author wfh
 * @Date 2022/12/4 下午4:12
 */
public class OutChannelTwo extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        System.out.println("OutChannelTwo");
        super.write(ctx,msg,promise);
    }
}
