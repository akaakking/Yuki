package org.xulinux.yuki.transport.handler;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.xulinux.yuki.common.fileUtil.FileSectionInfo;
import org.xulinux.yuki.common.fileUtil.ResourceMetadata;
import org.xulinux.yuki.registry.NodeInfo;
import org.xulinux.yuki.transport.Message;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 *
 *
 * @Author wfh
 * @Date 2022/10/14 上午9:20
 */
public class ClientDecoder extends ByteToMessageDecoder {
    private List<NodeInfo> nodeInfos;
    private ResourceMetadata metadata;

    // raf 要不要做个池嘞
    private RandomAccessFile raf;

    private int sectionLength;

    private State state;

    private String baseDir;

    public ClientDecoder() {
        state = State.HEAD_PARSE;
    }

    public void setNodeInfos(List<NodeInfo> nodeInfos) {
        this.nodeInfos = nodeInfos;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        switch (state) {
            case HEAD_PARSE:
                headParser(byteBuf,list);
                break;
            case FILE_TRANSFER:
                writeToDisk(byteBuf);
                break;
            default:
                break;
        }
    }

    private void writeToDisk(ByteBuf byteBuf) throws IOException {
        int readsize = byteBuf.readableBytes() > sectionLength ? byteBuf.readableBytes() : sectionLength;

        ByteBuffer buffer = byteBuf.internalNioBuffer(byteBuf.readerIndex(),readsize);
        byteBuf.skipBytes(readsize);

        // 零拷贝都是针对发送而言的吗，接受这边不可以用？
        raf.getChannel().write(buffer);

        sectionLength -= readsize;

        if (sectionLength == 0) {
            raf.close();
            this.state = State.HEAD_PARSE;
        }
    }

    private void headParser(ByteBuf byteBuf, List<Object> list) {
        if (byteBuf.readableBytes() < 4) {
            return;
        }

        byteBuf.markReaderIndex();

        int jsonSize = byteBuf.readInt();
        if (jsonSize < byteBuf.readableBytes()) {
            byteBuf.resetReaderIndex();
            return;
        }

        String json = byteBuf.toString(byteBuf.readerIndex(),jsonSize, StandardCharsets.UTF_8);

        Message message = JSON.parseObject(json,Message.class);

        switch (message.getType()) {
            case METADATA_RESPONSE:
                list.add(message.getMetadata());
                break;
            case File_HEAD_INFO:
                setFileSection(message.getFileSectionInfo());
                this.state = State.FILE_TRANSFER;
                break;
            default:
                break;
        }
    }

    private void setFileSection(FileSectionInfo fileSectionInfo) {
        sectionLength = fileSectionInfo.getLength();

        try {
            raf = new RandomAccessFile(baseDir + fileSectionInfo.getDirPath() + fileSectionInfo.getFileName(),"rw");
            raf.seek(fileSectionInfo.getOffset());
        } catch (FileNotFoundException e) {
            // 没有就创建了，为什么会这样嘞
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }

    /**
     * 相当于搞了一个状态机
     */
     enum State {
         /**
         * 作为客户端有可能接收到以下消息
         * 1. 接收到metadata
          *      distrbute后进行分发
         * 3. 接收到File相关信息
          *      creatFile后开始往里边写信息
          *      将状态置为filetransfer 开始写信息
         *
         */
         HEAD_PARSE,

        // 接收文件
        // 一直写
        // 写完之后再重新置为HEAD_PARSER
        FILE_TRANSFER
    }

    // 要不一次发 4k？
}

/**
 * 发送方：
 * int    |  String   |  bytes    |
 * size   |   content |    文件   |
 *
 * 现在还有一个问题要解决
 *
 * 扩容扩不了，消费消费不全这要怎么办。
 */
