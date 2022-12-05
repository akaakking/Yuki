package org.xulinux.yuki.transport.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.xulinux.yuki.common.BeanUtil;
import org.xulinux.yuki.common.JobMetaData;
import org.xulinux.yuki.common.fileUtil.FileSectionInfo;
import org.xulinux.yuki.common.recorder.FileReceiveRecorder;
import org.xulinux.yuki.transport.Message;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 *  不可共享
 *
 * @Author wfh
 * @Date 2022/10/14 上午9:20
 */
public class ClientDecoder extends ByteToMessageDecoder {
    private FileReceiveRecorder recorder;
    private FileSectionInfo currSection;
    // raf 要不要做个池嘞
    private RandomAccessFile raf;
    private int sectionLength;
    private State state;
    private ChannelHandlerContext ctx;
    private int fileSectionIndex;

    private JobMetaData jobMetaData;

    private BlockingQueue<FileReceiveRecorder> waitingJobs;

    public ClientDecoder() {
        state = State.HEAD_PARSE;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {
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

    /**
     * 要将这个东西看作整个传输的开始
     * 就是一定要做到高内聚低耦合。
     */
    public void sendJob(FileReceiveRecorder recorder) {
        this.jobMetaData = recorder.getJobMetaData();
        this.recorder = recorder;

        Message message = new Message();
        message.setType(Message.Type.FILE_SECTION_ASSIGN);
        message.setResourceId(jobMetaData.getResourceId());
        message.setSectionInfos(jobMetaData.getSectionInfos());
        // 这个不是在io线程做的发送所以会包装成tast
        // 这边为什么发不过去
        try {
            ctx.channel().writeAndFlush(message).sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        reset();
    }

    private void reset() {
       this.fileSectionIndex = 0;
       this.state = State.HEAD_PARSE;
    }

    private void writeToDisk(ByteBuf byteBuf) throws IOException, InterruptedException {
        int readsize = byteBuf.readableBytes() < sectionLength ? byteBuf.readableBytes() : sectionLength;

        ByteBuffer buffer = byteBuf.internalNioBuffer(byteBuf.readerIndex(),readsize);
        raf.getChannel().write(buffer);
        byteBuf.skipBytes(readsize);

        recorder.record(readsize);

        sectionLength -= readsize;

        if (sectionLength == 0) {
            // todo raf 有可能未关闭
            raf.close();
            this.state = State.HEAD_PARSE;

            // 查看是否完全传输完毕，之后重新复用这条连接
            if (this.fileSectionIndex == this.jobMetaData.getSectionInfos().size() - 1) {
                if (!waitingJobs.isEmpty()) {
                    FileReceiveRecorder receiveRecorderrecorder = waitingJobs.take();

                    sendJob(receiveRecorderrecorder);
                }
            }
        }
    }

    private void headParser(ByteBuf byteBuf, List<Object> list) {
        if (byteBuf.readableBytes() < 4) {
            return;
        }

        byteBuf.markReaderIndex();

        int jsonSize = byteBuf.readInt();
        if (jsonSize > byteBuf.readableBytes()) {
            byteBuf.resetReaderIndex();
            return;
        }

        String json = byteBuf.toString(byteBuf.readerIndex(),jsonSize, StandardCharsets.UTF_8);
        byteBuf.skipBytes(jsonSize);
// {"metadata":{},"type":1} ?
        Message message = BeanUtil.getGson().fromJson(json,Message.class);

        switch (message.getType()) {
            case METADATA_RESPONSE:
                list.add(message.getMetadata());
                break;
            case File_HEAD_INFO:
                resetFileSection(message.getSectionIndex());
                this.state = State.FILE_TRANSFER;
                break;
            default:
                break;
        }
    }

    private void resetFileSection(int fileSectionIndex) {
        this.fileSectionIndex = fileSectionIndex;
        currSection = jobMetaData.getSectionInfos().get(fileSectionIndex);
        sectionLength = currSection.getLength();

        try {
            raf = new RandomAccessFile(jobMetaData.getDownDir() + "/" + currSection.getDirPath() + "/" + currSection.getFileName(),"rw");
            raf.seek(currSection.getOffset());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setWaitingJobs(BlockingQueue<FileReceiveRecorder> waitingJobs) {
        this.waitingJobs = waitingJobs;
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
}

/**
 * 理一下思路
 * 客户端    请求metadata
 * 服务器这边 返回metadata
 * 客户端 接收metadata之后distribute发送给多个服务器，但是也要做一个分发相当于。
 * 服务器这边接收到自己的那一份要保存起来
 */
