package org.xulinux.yuki;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.junit.Test;

import java.awt.dnd.DropTarget;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.PriorityQueue;

/**
 * //TODO add class commment here
 *
 * @Author wfh
 * @Date 2022/10/14 下午2:48
 */
public class Encoder extends MessageToByteEncoder<Message> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Message message, ByteBuf byteBuf) throws Exception {
        // 有一个很奇怪的点就是按理来说这个东西不应该可以扩容要不然地址会变啊？
        String jsobj = JSON.toJSONString(message);
        byte[] bytes = jsobj.getBytes(StandardCharsets.UTF_8);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }

    @Test
    public void tsgg() {
        File file = new File("/home/wfh/dubbo/target/maven-shared-archive-resources/META-INF/LICENSE");

        System.out.println(file.getAbsolutePath());
        System.out.println(file.getParent());
    }
}

