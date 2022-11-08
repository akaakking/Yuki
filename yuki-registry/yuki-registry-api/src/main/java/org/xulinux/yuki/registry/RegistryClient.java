package org.xulinux.yuki.registry;

import java.util.List;
import java.util.Set;

/**
 * //TODO add interface commment here
 *
 * @Author wfh
 * @Date 2022/10/8 下午7:59
 */
public interface RegistryClient {
    void registerResources(String resourceId,NodeInfo nodeInfo);
    List<NodeInfo> getResouceHolders(String resourceId);
    void unRegisterResources(String resourceId,NodeInfo nodeInfo);
    void setRegistryHost(String ip,int port);
    void destry();
    void connect();
    List<String> searchResource(String nameStart);
}
