package org.xulinux.yuki.registry;

import org.xulinux.yuki.common.NodeInfo;

import java.util.List;

/**
 * //TODO add interface commment here
 *
 * @Author wfh
 * @Date 2022/10/8 下午7:59
 */
public interface RegistryClient {
    void registerResources(String resourceId, NodeInfo nodeInfo);
    List<NodeInfo> getResouceHolders(String resourceId);
    void unRegisterResources(String resourceId,NodeInfo nodeInfo);
    void destry();
    void connect(String ip, int port);
    List<String> searchResource(String nameStart);
}
