package org.xulinux.yuki;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.xulinux.yuki.common.BeanUtil;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * //TODO add class commment here
 *
 * @Author wfh
 * @Date 2022/10/14 下午2:47
 */
public class Decoder extends ByteToMessageDecoder {
    private long fileSize = 0;
    private FileChannel fileChannel;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (fileSize == 0) {
            byteBuf.markReaderIndex();
            int size = byteBuf.readInt();
            if (byteBuf.readableBytes() < size) {
                byteBuf.resetReaderIndex();
                return;
            }
            
            // 这个方法其实很讲究可以看一下是怎么做的
            String json = byteBuf.toString(byteBuf.readerIndex(),size,StandardCharsets.UTF_8);

            Message message = BeanUtil.getGson().fromJson(json,Message.class);
            System.out.println("正在接受" + message.getFileName());

            fileSize = message.getFileSize();
            fileChannel = new RandomAccessFile("/home/wfh/" + message.getFileName(),"rw").getChannel();
            return;
        }

        ByteBuffer byteBuffer = byteBuf.internalNioBuffer(byteBuf.readerIndex(),byteBuf.readableBytes());
        fileChannel.write(byteBuffer);
        System.out.println("已接收 " + (fileChannel.size() * 1.0 / fileSize) * 100 + "%");
        byteBuf.skipBytes(byteBuf.readableBytes());
    }
}

/**
 @Test
 public void raf() {
 // 需要加锁保护raf不过这样的话就串行话了
 try {
 RandomAccessFile raf = new RandomAccessFile("/home/wfh/sim","rw");
 raf.write(2);
 } catch (FileNotFoundException e) {
 e.printStackTrace();
 } catch (IOException e) {
 e.printStackTrace();
 }
 }

/**
 * 结论
 * 确实是不能更新的，相当于tcp的一个接受buffer,但它是有容量限制的这种。
 * 为什么不抽象成一直更新的这种呢 (可以return就相当于是一直更新了，因为他是水平触发，只要缓冲区有东西就会一直相应) ，如果是这种的话就很好搞了
 * 关键是这个样子的话，我们文件传送该如何搞嘞
 *
 *
 * chunk 看完就直接写吧
 *
 */
