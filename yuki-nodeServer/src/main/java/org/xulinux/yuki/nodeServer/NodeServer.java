package org.xulinux.yuki.nodeServer;

import org.xulinux.yuki.registry.LoadBalance;
import org.xulinux.yuki.registry.NodeInfo;
import org.xulinux.yuki.registry.RegistryClient;
import org.xulinux.yuki.transport.TransportClient;
import org.xulinux.yuki.transport.TransportServer;

import java.util.List;
import java.util.concurrent.CountDownLatch;


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

    private LoadBalance balance;

    private TransportServer transportServer;

    private TransportClient transportClient;

    private String zkIp;

    private int zkPort;

    private String aofPath;


    /**
     * 自身节点信息
     * todo 未初始化
     */
    private NodeInfo nodeInfo;

    public NodeServer() {
    }

    public void setAofPath(String aofPath) {
        this.aofPath = aofPath;
    }

    public void setNodeInfo(NodeInfo nodeInfo) {
        this.nodeInfo = nodeInfo;
    }

    public void setZkIp(String zkIp) {
        this.zkIp = zkIp;
    }

    public void setZkPort(int zkPort) {
        this.zkPort = zkPort;
    }

    public void exportService() {
        // 初始化registryClient
        registryClient.connect();

        // 开启netty接收请求
        transportServer.start();
    }

    public void terminal() {
        // 关闭registryClient
        registryClient.destry();

        // 关闭netty监听服务
        transportServer.terminal();
    }

    public void registerResourth(String resourthID) {
        registryClient.registerResources(resourthID,nodeInfo);
    }

    public void unregisterResourth(String resourthID) {
        registryClient.unRegisterResources(resourthID,nodeInfo);
    }

    /**
     * 下载 资源
     */
    public void download(String resourceId) {
        List<NodeInfo> resourceHolders = registryClient.getResouceHolders(resourceId);

        int maxReceive = 20;

        resourceHolders = balance.select(resourceHolders,maxReceive);

        transportClient.download(resourceId,resourceHolders);

        // down
        registerResourth(resourceId);
    }
}
