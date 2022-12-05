package org.xulinux.yuki.transport.handler;

import io.netty.channel.*;
import org.xulinux.yuki.common.recorder.FileReceiveRecorder;

import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * //TODO add class commment here
 *
 * @Author wfh
 * @Date 2022/12/4 下午6:28
 */
public class DuplexExceptionHandler extends ChannelDuplexHandler {
    private BlockingQueue<FileReceiveRecorder> waitingJobs;
    private FileReceiveRecorder recorder;

    public void setWaitingJobs(BlockingQueue<FileReceiveRecorder> waitingJobs) {
        waitingJobs = waitingJobs;
    }

    public void setRecorder(FileReceiveRecorder recorder) {
        this.recorder = recorder;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        promise.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    waitingJobs.add(recorder);
                    future.channel().close();
                }
            }
        });

        super.write(ctx,msg,promise);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        this.waitingJobs.add(recorder);
        ctx.close();
    }
}
