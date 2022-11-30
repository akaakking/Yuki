package org.xulinux.yuki.transport;

import org.xulinux.yuki.common.JobMetaData;
import org.xulinux.yuki.common.NodeInfo;
import org.xulinux.yuki.common.ProgressBar;
import org.xulinux.yuki.common.Speaker;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

/**
 * //TODO add class commment here
 *
 * @Author wfh
 * @Date 2022/10/11 下午4:33
 */
public interface TransportClient {

    // 可不可以搞成异步的呢？
    void download(List<NodeInfo> resouceHolders, JobMetaData jobMetaData, ProgressBar progressBar);
    // todo
    void resumeTransmission(List<NodeInfo> resouceHolders, File[] logs);
    void rmLogAndResource();
}

