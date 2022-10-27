package org.xulinux.yuki;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;


/**
 * //TODO add class commment here
 *
 * @Author wfh
 * @Date 2022/10/14 下午2:48
 */
public class NettyClient {

    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        bootstrap.group(eventLoopGroup)
                .option(ChannelOption.SO_RCVBUF,1024 * 8)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                socketChannel.pipeline()
                                        .addLast(new Decoder());
                            }
                        });
                    }
                });

        try {
            ChannelFuture connectfu = bootstrap.connect("127.0.0.1", 9140).sync();
            connectfu.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    System.out.println("连接成功");
                }
            });
            connectfu.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

//    public static void main(String[] args) {
//        Bootstrap bootstrap = new Bootstrap();
//        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
//
//        bootstrap.group(eventLoopGroup)
//                .channel(NioSocketChannel.class)
//                .handler(new ChannelInitializer<SocketChannel>() {
//                    @Override
//                    protected void initChannel(SocketChannel socketChannel) throws Exception {
//                        socketChannel.pipeline().addLast(new Encoder());
//                    }
//                });
//        ChannelFuture connect = null;
//        try {
//            connect = bootstrap.connect("127.0.0.1", 9140).sync();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        connect.addListener(new ChannelFutureListener() {
//            @Override
//            public void operationComplete(ChannelFuture channelFuture) throws Exception {
//                System.out.println("连接成功");
//            }
//        });
////
////        for (int i = 0; i < 100; i++) {
////            Message message = new Message();
////            StringBuilder str = new StringBuilder();
////
////            str.append(i % 10);
////
////            str.append("hello world");
////
////            for (int j = i % 10; j >= 0; j--) {
////                str.append(" hello word");
////            }
////
////            message.setSize(str.length());
////            message.setContent(str.toString());
////
////            connect.channel().writeAndFlush(message);
////        }
////        try {
////            new CountDownLatch(1).await();
////        } catch (InterruptedException e) {
////            e.printStackTrace();
////        }
//    }
}