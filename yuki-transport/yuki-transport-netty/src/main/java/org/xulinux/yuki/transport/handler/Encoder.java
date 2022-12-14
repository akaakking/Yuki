package org.xulinux.yuki.transport.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.xulinux.yuki.common.BeanUtil;
import org.xulinux.yuki.common.recorder.FileReceiveRecorder;
import org.xulinux.yuki.transport.Message;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * //TODO add class commment here
 *
 * @Author wfh
 * @Date 2022/10/20 下午1:34
 */
@ChannelHandler.Sharable
public class Encoder extends MessageToByteEncoder<Message> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Message message, ByteBuf byteBuf) throws Exception {
        String json = BeanUtil.getGson().toJson(message);

        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }

}


