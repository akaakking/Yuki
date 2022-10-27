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

/**
 * //TODO add class commment here
 *
 * @Author wfh
 * @Date 2022/10/27 下午1:38
 */
public class FileTransferHandler extends ChannelInboundHandlerAdapter {
    private ConcurrentHashMap<String,String> id2path;
    private int chunksize = 1 << (10 + 3);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message) msg;

        List<FileSectionInfo> fileSectionInfos = message.getSectionInfos();

        if (fileSectionInfos == null) {
            return;
        }

        String resourthPath = id2path.get(message.getResourceId());

        boolean isLinux = false;

        String osName = System.getProperty("os.name");

        if (osName != null && osName.toLowerCase().startsWith("linux")) {
            isLinux = true;
        }

        for (FileSectionInfo fileSectionInfo : fileSectionInfos) {
            Message responseHead = new Message();

            responseHead.setType(Message.Type.File_HEAD_INFO);
            responseHead.setFileSectionInfo(fileSectionInfo);

            ctx.write(responseHead);

            String sectionPath = resourthPath + "/" + fileSectionInfo.getDirPath() + "/" + fileSectionInfo.getFileName();

            RandomAccessFile raf = new RandomAccessFile(sectionPath,"r");

            if (isLinux) {
                ctx.write(new DefaultFileRegion(raf.getChannel(),fileSectionInfo.getOffset(),fileSectionInfo.getLength()));
            } else {
                ctx.write(new ChunkedFile(raf,fileSectionInfo.getOffset(),fileSectionInfo.getLength(),chunksize));
            }

        }
    }
}
