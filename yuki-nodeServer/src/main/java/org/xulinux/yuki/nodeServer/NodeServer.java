package org.xulinux.yuki.nodeServer;

import org.xulinux.yuki.registry.NodeInfo;
import org.xulinux.yuki.registry.RegistryClient;

import java.nio.IntBuffer;

/**
 * //TODO add interface commment here
 *
 * @Author wfh
 * @Date 2022/10/10 下午5:40
 */
public class NodeServer {
    /**
     * zk 句柄
     * todo 未初始化
     */
    private RegistryClient registryClient;

    /**
     * 自身节点信息
     * todo 未初始化
     */
    private NodeInfo nodeInfo;

    public NodeServer() {
    }

    public void start() {
        // 初始化registryClient
        registryClient.connect();

        // 开启netty接收请求

    }

    public void terminal() {
        // 关闭registryClient
        registryClient.destry();

        // 关闭netty监听服务

    }

    public void registerResourth(String resourthID) {
        registryClient.registerResources(resourthID,nodeInfo);
    }

    public void unregisterResourth(String resourthID) {
        registryClient.unRegisterResources(resourthID,nodeInfo);
    }

    // regis  就相当于上传upload
    public void download() {

    }
}
