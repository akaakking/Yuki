package org.xulinux.yuki.transport.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.DefaultFileRegion;
import io.netty.handler.stream.ChunkedFile;
import org.xulinux.yuki.common.fileUtil.FileSectionInfo;
import org.xulinux.yuki.transport.Message;

import java.io.RandomAccessFile;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * //TODO add class commment here
 *
 * @Author wfh
 * @Date 2022/10/27 下午1:38
 */
public class FileTransferHandler extends ChannelInboundHandlerAdapter {
    private ConcurrentHashMap<String,String> id2path;
    private int chunksize = 1 << (10 + 3);
    private AtomicInteger transCount;

    public FileTransferHandler(AtomicInteger transCount) {
        this.transCount = transCount;
    }

    public void setId2path(ConcurrentHashMap<String, String> id2path) {
        this.id2path = id2path;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message) msg;

        List<FileSectionInfo> fileSectionInfos = message.getSectionInfos();

        if (fileSectionInfos == null) {
            return;
        }

        transCount.getAndIncrement();

        String resourthPath = id2path.get(message.getResourceId());

        boolean isLinux = false;

        String osName = System.getProperty("os.name");

        if (osName != null && osName.toLowerCase().startsWith("linux")) {
            isLinux = true;
        }

        for (int index =  0; index < fileSectionInfos.size(); index++) {
            FileSectionInfo fileSectionInfo = fileSectionInfos.get(index);
            Message responseHead = new Message();

            responseHead.setType(Message.Type.File_HEAD_INFO);
            responseHead.setSectionIndex(index);

            ctx.write(responseHead);

            String sectionPath = resourthPath + "/" + fileSectionInfo.getDirPath() + "/" + fileSectionInfo.getFileName();

            RandomAccessFile raf = new RandomAccessFile(sectionPath,"r");

            if (isLinux) {
                ctx.writeAndFlush(new DefaultFileRegion(raf.getChannel(),fileSectionInfo.getOffset(),fileSectionInfo.getLength()));
            } else {
                ctx.writeAndFlush(new ChunkedFile(raf,fileSectionInfo.getOffset(),fileSectionInfo.getLength(),chunksize));
            }

        }

        transCount.decrementAndGet();
    }
}
