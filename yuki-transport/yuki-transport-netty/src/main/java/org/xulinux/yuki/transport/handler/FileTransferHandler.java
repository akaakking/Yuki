package org.xulinux.yuki.transport.handler;

import io.netty.channel.*;
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
public class FileTransferHandler extends SimpleChannelInboundHandler<Message> {
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
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        List<FileSectionInfo> fileSectionInfos = msg.getSectionInfos();

        if (fileSectionInfos == null) {
            return;
        }

        transCount.getAndIncrement();

        String resourthPath = id2path.get(msg.getResourceId());
        String rootPath = resourthPath.substring(0,resourthPath.lastIndexOf("/"));

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

            String sectionPath = rootPath + "/" + fileSectionInfo.getDirPath() + "/" + fileSectionInfo.getFileName();

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
