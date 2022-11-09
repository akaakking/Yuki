package org.xulinux.yuki.transport.handler;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.xulinux.yuki.common.fileUtil.FileSectionInfo;
import org.xulinux.yuki.common.fileUtil.ResourceMetadata;
import org.xulinux.yuki.common.recorder.Recorder;
import org.xulinux.yuki.registry.NodeInfo;
import org.xulinux.yuki.transport.Message;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 *  不可共享
 *
 * @Author wfh
 * @Date 2022/10/14 上午9:20
 */
public class ClientDecoder extends ByteToMessageDecoder {
    private List<NodeInfo> nodeInfos;
    private ResourceMetadata metadata;
    private Recorder recorder;

    private List<FileSectionInfo> fileSectionInfos;

    private FileSectionInfo currSection;

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

        recorder.record(readsize);

        sectionLength -= readsize;

        if (sectionLength == 0) {
            raf.close();
            this.state = State.HEAD_PARSE;
        }
    }

    public void setRecorder(Recorder recorder) {
        this.recorder = recorder;
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
                setFileSection(message.getSectionIndex());
                this.state = State.FILE_TRANSFER;
                break;
            default:
                break;
        }
    }

    private void setFileSection(int fileSectionIndex) {
        currSection = fileSectionInfos.get(fileSectionIndex);
        sectionLength = currSection.getLength();

        recorder.setFile(fileSectionIndex);

        try {
            raf = new RandomAccessFile(baseDir + currSection.getDirPath() + currSection.getFileName(),"rw");
            raf.seek(currSection.getOffset());

            recorder.setFile(fileSectionIndex);
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

    public void setFileSectionInfos(List<FileSectionInfo> fileSectionInfos) {
        this.fileSectionInfos = fileSectionInfos;
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
 * 理一下思路
 * 客户端    请求metadata
 * 服务器这边 返回metadata
 * 客户端 接收metadata之后distribute发送给多个服务器，但是也要做一个分发相当于。
 * 服务器这边接收到自己的那一份要保存起来
 */
