package org.xulinux.yuki.transport;

import org.xulinux.yuki.registry.NodeInfo;

import java.util.List;

/**
 * //TODO add class commment here
 *
 * @Author wfh
 * @Date 2022/10/11 下午4:33
 */
public interface TransportClient {

    // 可不可以搞成异步的呢？
    void download(String resourceId, List<NodeInfo> resouceHolders);
}
