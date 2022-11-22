package org.xulinux.yuki.nodeServer;

import io.netty.bootstrap.Bootstrap;
import org.xulinux.yuki.common.Listenner;
import org.xulinux.yuki.common.Speaker;
import org.xulinux.yuki.common.fileUtil.FileUtil;
import org.xulinux.yuki.common.spi.ExtensionLoader;
import org.xulinux.yuki.registry.LoadBalance;
import org.xulinux.yuki.registry.NodeInfo;
import org.xulinux.yuki.registry.RegistryClient;
import org.xulinux.yuki.transport.TransportClient;
import org.xulinux.yuki.transport.TransportServer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * //TODO add interface commment here
 *
 * @Author wfh
 * @Date 2022/10/10 下午5:40
 */
public class NodeServer implements Speaker {
    private final static String id2PathFilePath = "id2path.log";

    /**
     * 资源id -> 资源path
     */
    private static final ConcurrentHashMap<String, String> id2path = new ConcurrentHashMap<>();

    /**
     * 注册中心的客户端，用来资源发现
     */
    private RegistryClient registryClient;
    private List<Listenner> listenners;

    private volatile boolean start;

    private TransportServer transportServer;

    private TransportClient transportClient;

    private String hostString;

    // set
    private String aofPath;

    /**
     * 自身节点信息
     * todo 未初始化
     */
    private NodeInfo nodeInfo;

    public NodeServer() {
        listenners = new ArrayList<>();
        transportClient = ExtensionLoader.getExtension(TransportClient.class);
    }

    private void ininID2Path() {
        File file = new File(aofPath + "/" + id2PathFilePath);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        List<String> list = FileUtil.readList(aofPath);
        // key%value
        for (String s : list) {
            String[] kv = s.split("%");

            id2path.put(kv[0],kv[1]);
        }
    }

    public void setHostString(String hostString) {
        this.hostString = hostString;
    }

    private void ininTransportServer() {
        transportServer = ExtensionLoader.getExtension(TransportServer.class);

        transportServer.setPort(nodeInfo.getPort());
    }

    public void setAofPath(String aofPath) {
        this.aofPath = aofPath;
    }

    public AtomicInteger getTransCount() {
        return transportServer.transporting();
    }

    public void setNodeInfo(NodeInfo nodeInfo) {
        this.nodeInfo = nodeInfo;
    }

    public boolean isStart() {
        return start;
    }

    public void exportService() {
        if (start) {
            this.speak("服务已开启，不可重复启动");
            return;
        }

        this.speak("正在连接zk...");
        // 初始化registryClient
        registryClient.connect();

        this.speak("zk连接成功！");


        // 开启netty接收请求
        this.speak("正在开启服务....");
        transportServer.start();
        this.speak("服务已开启");
        this.start = true;
    }

    public void ininServer() {
        ininID2Path();
        ininTransportServer();
    }

    public void terminal() {
        this.speak("正在关闭服务...");

        this.speak("正在关闭zk...");
        // 关闭registryClient
        registryClient.destry();
        this.speak("zk 关闭成功");

        this.speak("正在关闭传输服务...");
        // 关闭netty监听服务
        transportServer.terminal();
        this.speak("传输服务关闭成功");
    }

    public void registerResourth(String resourthID) {
        this.speak("正在注册服务[" + resourthID +
                "] 。。。");
        registryClient.registerResources(resourthID,nodeInfo);
        this.speak("服务[" + resourthID +
                "]注册成功！");
    }

    public void unregisterResourth(String resourthID) {
        this.speak("正在反注册服务[" + resourthID +
                "]。。。");
        registryClient.unRegisterResources(resourthID,nodeInfo);
        this.speak("反注册服务[" + resourthID +
                "]成功！");
    }

    /**
     * 下载 资源
     */
    public void download(String resourceId) {
        this.speak("准备下载资源[" + resourceId + "]");

        List<NodeInfo> resourceHolders = registryClient.getResouceHolders(resourceId);

        if (resourceHolders == null || resourceHolders.isEmpty()) {
            this.speak("集群中没有此资源["+ resourceId +"]");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("有 " + resourceHolders.size() + " 个资源拥有者\n");

        int maxReceive = nodeInfo.getMaxServicing();

        LoadBalance balance = ExtensionLoader.getExtension(LoadBalance.class);

        resourceHolders = balance.select(resourceHolders,maxReceive);

        sb.append("挑选出来了" + resourceHolders.size() + "个资源拥有者\n");

        for (NodeInfo resourceHolder : resourceHolders) {
            sb.append(resourceHolder)
                    .append("\n");
        }
        this.speak(sb.toString());

        this.speak("正在向资源拥有者们请求....");
        transportClient.download(resourceId,resourceHolders);
        this.speak("文件下载成功！");

        this.speak("正在注册服务...");
        // down
        registerResourth(resourceId);
        this.speak("注册服务成功...");
    }

    @Override
    public void speak(String message) {
        for (Listenner listenner : listenners) {
            listenner.messageFromSpeaker(message);
        }
    }

    @Override
    public void addListenner(Listenner listenner) {
        listenners.add(listenner);
    }

    @Override
    public void removeListenner(Listenner listenner) {
        listenners.remove(listenner);
    }
}
