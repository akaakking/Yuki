package org.xulinux.yuki;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import org.junit.Test;
import org.xulinux.yuki.common.BeanUtil;
import org.xulinux.yuki.common.fileUtil.ResourceMetadata;
import org.xulinux.yuki.transport.Message;

/**
 * //TODO add class commment here
 *
 * @Author wfh
 * @Date 2022/11/28 上午11:19
 */
public class Telenet {
    public static void main(String[] args)  {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup child = new NioEventLoopGroup();




        serverBootstrap.group(boss,child)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder()).addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                        int i = 0;
                                        while (true) {
                                                ctx.writeAndFlush("\rfduhfuisdhfud" + i++);

                                            try {
                                                Thread.sleep(500);
                                            } catch (InterruptedException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                            }
                        });
                    }
                });

        ChannelFuture bind = serverBootstrap.bind(9410);
        try {
            bind.sync().channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            boss.shutdownGracefully();
            child.shutdownGracefully();
        }

    }


    @Test
    public void tees() {
        Message message = new Message();
        ResourceMetadata resourceMetadata = new ResourceMetadata("/home/wfh/Downloads/feifei");
        message.setMetadata(resourceMetadata);

        String json = BeanUtil.getGson().toJson(message);

        Message message1 = BeanUtil.getGson().fromJson(json, Message.class);
        int len = json.length();

        System.out.println(len);
    }
}
