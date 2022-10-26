package org.xulinux.yuki.transport.handler;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.xulinux.yuki.transport.Message;

import java.nio.charset.StandardCharsets;

/**
 * //TODO add class commment here
 *
 * @Author wfh
 * @Date 2022/10/20 下午1:34
 */
public class Encoder extends MessageToByteEncoder<Message> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Message message, ByteBuf byteBuf) throws Exception {
        String json = JSON.toJSONString(message);
        byteBuf.writeInt(json.length());

        byteBuf.writeBytes(json.getBytes(StandardCharsets.UTF_8));
    }
}


