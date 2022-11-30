package org.xulinux.yuki.transport.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.xulinux.yuki.common.BeanUtil;
import org.xulinux.yuki.transport.Message;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * //TODO add class commment here
 *
 * @Author wfh
 * @Date 2022/10/19 下午12:14
 */
public class ServerDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() < 4) {
            return;
        }

        byteBuf.markReaderIndex();

        int jsonSize = byteBuf.readInt();

        if (byteBuf.readableBytes() < jsonSize) {
            byteBuf.resetReaderIndex();
            return;
        }

        String json = byteBuf.toString(byteBuf.readerIndex(),jsonSize, StandardCharsets.UTF_8);

        Message message = BeanUtil.getGson().fromJson(json,Message.class);

        list.add(message);
    }
}
