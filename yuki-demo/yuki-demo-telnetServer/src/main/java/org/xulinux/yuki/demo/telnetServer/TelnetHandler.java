package org.xulinux.yuki.demo.telnetServer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.xulinux.yuki.common.Listenner;
import org.xulinux.yuki.common.YUKI;
import org.xulinux.yuki.nodeServer.NodeServer;

/**
 * //TODO add class commment here
 *
 * @Author wfh
 * @Date 2022/10/29 下午10:04
 */
public class TelnetHandler extends SimpleChannelInboundHandler<String> implements Listenner {
    public static final String PROTMT = "YUKI> ";
    private NodeServer nodeServer;
    private ChannelHandlerContext ctx;

    public TelnetHandler(NodeServer nodeServer) {
        this.nodeServer = nodeServer;
        this.nodeServer.addListenner(this);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String message) throws Exception {
        CommandExecutor.execute(message);

        ctx.writeAndFlush(PROTMT);
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        CommandExecutor.setNodeServer(nodeServer);
        CommandExecutor.setCtx(ctx);
        ctx.writeAndFlush(YUKI.YUKI_LOGO + "\r\n");
        nodeServer.checkDowntime();
        ctx.writeAndFlush(PROTMT);
    }

    @Override
    public void messageFromSpeaker(String message) {
        if (message.startsWith("\r")) {
            this.ctx.writeAndFlush(message);
        } else {
            this.ctx.writeAndFlush("      " + message + "\n");
        }
    }
}
