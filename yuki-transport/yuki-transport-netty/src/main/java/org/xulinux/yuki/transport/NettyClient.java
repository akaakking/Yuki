package org.xulinux.yuki.transport;

import org.xulinux.yuki.registry.NodeInfo;

import java.util.List;

/**
 * //TODO add class commment here
 *
 * @Author wfh
 * @Date 2022/10/11 下午4:33
 */
public class NettyClient implements TransportClient{

    @Override
    public void download(String resourceId, List<NodeInfo> recipient/* 接受者*/) {
        int recipientCount = recipient.size();

        // 首先和一个资源拥有者进行沟通

    }


}
