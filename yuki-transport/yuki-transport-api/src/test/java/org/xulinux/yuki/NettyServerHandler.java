package org.xulinux.yuki;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.stream.ChunkedFile;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;

/**
 * //TODO add class commment here
 * /home/wfh/Downloads/Netty权威指南 PDF电子书下载 带目录书签 完整版.pdf
 * @Author wfh
 * @Date 2022/10/14 下午3:20
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("开始发送。。。");

        RandomAccessFile raf = new RandomAccessFile("/home/wfh/Downloads/Netty权威指南 PDF电子书下载 带目录书签 完整版.pdf","r");

        Message message = new Message();
        message.setFileName("Netty权威指南 PDF电子书下载 带目录书签 完整版.pdf");
        message.setFileSize(raf.length());

        ctx.write(message);

        // 直接发送
        ctx.writeAndFlush(new DefaultFileRegion(raf.getChannel(),0,raf.length()));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        System.out.println(cause);
    }
}

/**
 * k8s 如何调度微服务
 * sidecar（数据面负责传输数据 就是 istio中的envoy
 * istio 和k8s如何配合
 */
