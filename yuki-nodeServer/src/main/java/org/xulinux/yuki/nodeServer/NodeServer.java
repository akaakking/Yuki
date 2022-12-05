package org.xulinux.yuki.nodeServer;

import org.xulinux.yuki.common.*;
import org.xulinux.yuki.common.fileUtil.FileUtil;
import org.xulinux.yuki.common.recorder.ResourcePathRecorder;
import org.xulinux.yuki.common.spi.ExtensionLoader;
import org.xulinux.yuki.registry.LoadBalance;
import org.xulinux.yuki.registry.RegistryClient;
import org.xulinux.yuki.transport.TransportClient;
import org.xulinux.yuki.transport.TransportServer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * //TODO add interface commment here
 *
 * @Author wfh
 * @Date 2022/10/10 下午5:40
 */
public class NodeServer implements Speaker {
    private boolean hasDowntime;

    /**
     * 资源id -> 资源path
     */
    public static final ConcurrentHashMap<String, String> id2path = new ConcurrentHashMap<>();

    /**
     * 注册中心的客户端，用来资源发现
     */
    private RegistryClient registryClient;
    private List<Listenner> listenners;

    private volatile boolean servicing;

    private TransportServer transportServer;

    private TransportClient transportClient;

    private String hostString;

    private String aofPath;

    /**
     * 自身节点信息
     * todo 未初始化
     */
    private NodeInfo nodeInfo;

    public NodeServer(String aofPath,
                      NodeInfo nodeInfo,
                      String hostString) {
        this.aofPath = aofPath;
        this.nodeInfo = nodeInfo;
        this.hostString = hostString;
        listenners = new ArrayList<>();
        transportClient = ExtensionLoader.getExtension(TransportClient.class);

        initID2Path();
        initRegistry();
    }

    public void checkDowntime() {
        File file = new File(this.aofPath);
        String[] list = file.list();

        if (list != null && list.length > 1 ) {
            this.speak("由于上次宕机，存在未传输完成资源，是否继续传输（y/n）\n");
            this.hasDowntime = true;
        }
    }

    public boolean hasDowntime() {
        return hasDowntime;
    }

    public void rmLogAndResource() {
        this.transportClient.rmLogAndResource();
    }

    private void initTransportServer() {
        transportServer = ExtensionLoader.getExtension(TransportServer.class);

        transportServer.setPort(nodeInfo.getPort());
    }

    public List<String> searchResources(String prefix) {
        return this.registryClient.searchResource(prefix);
    }

    public AtomicInteger getTransCount() {
        return transportServer.transporting();
    }

    public boolean isServicing() {
        return servicing;
    }

    public void exportService() {
        if (servicing) {
            this.speak("服务已开启，不可重复启动");
            return;
        }

        this.servicing = true;

        initTransportServer();

        for (String resourceID : id2path.keySet()) {
            this.speak("向注册中心注册：" + resourceID);
            registToRegistry(resourceID);
        }

        // 开启netty接收请求
        this.speak("正在开启服务....");
        transportServer.start(id2path);
        this.speak("服务已开启");
        this.servicing = true;
    }

    public void registLocal(String resourceId, String path) {
        id2path.put(resourceId,path);
        persist(resourceId,path);
    }

    public void registToRegistry(String resourthID) {
        registryClient.registerResources(resourthID,nodeInfo);
    }

    public void terminal() {
        this.speak("正在关闭服务...");

        this.speak("正在关闭zk...");
        // 关闭registryClient
        registryClient.destry();
        this.speak("zk 关闭成功");

        this.speak("正在关闭传输服务...");
        // 关闭netty监听服务
        if (transportServer != null) {
            transportServer.terminal();
        }
        transportClient.shutdown();
        this.speak("传输服务关闭成功");
    }

    public void registerResourth(String resourthID, String path) {
        if (!isServicing()) {
            this.speak("服务未开启，请先开启服务后再注册");
            return;
        }

        this.speak("正在注册服务[" + resourthID +
                "] 。。。");

        registToRegistry(resourthID);

        this.speak("服务[" + resourthID +
                "]注册成功！");

        registLocal(resourthID,path);
    }

    private void persist(String resourceId, String path) {
        File id2pathlog = new File(ResourcePathRecorder.getAofDirPath() + ResourcePathRecorder.id2pathFileName);
        FileUtil.writeLine(id2pathlog,resourceId + "%" + path);
    }

    /**
     * 除了 unregist还应该有一个，取消这个资源服务的接口。
     * @param resourthID
     */
    public void unregisterResourth(String resourthID) {
        this.speak("正在反注册服务[" + resourthID +
                "]。。。");
        registryClient.unRegisterResources(resourthID,nodeInfo);
        this.speak("反注册服务[" + resourthID +
                "]成功！");
        // todo 1. hashmap delete 2. log delete
    }

    public boolean shutdown(boolean force) {
        if (servicing && !force) {
            speak("有任务正在运行，如果要强制退出，请用 -force");
            return false;
        }

        this.speak("bye~");

        terminal();

        return true;
    }

    /**
     * 下载 资源
     */
    public void download(String resourceId,String downDir) {
        if (!new File(downDir).exists()) {
            this.speak("本机不存在目录：" + downDir);
            return;
        }

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

        sb.append("      挑选出来了" + resourceHolders.size() + "个资源拥有者\n");

        for (NodeInfo resourceHolder : resourceHolders) {
            sb.append("      " + resourceHolder)
                    .append("\n");
        }

        this.speak(sb.delete(sb.length()-1,sb.length()).toString());

        this.speak("正在向资源拥有者们请求....");

        JobMetaData jobMetaData = new JobMetaData(resourceId,downDir);

        ProgressBar progressBar = new ProgressBar();
        progressBar.setSpeaker(this);

        transportClient.download(resourceHolders, jobMetaData,progressBar);

        this.speak("文件下载成功！");

        this.speak("正在注册服务...");
        // down
        registLocal(resourceId,jobMetaData.getDownDir() + "/" + jobMetaData.getResourceDirName());

        if (servicing) {
            registToRegistry(resourceId);
        }

        this.speak("注册服务成功...");
    }

    public void resumeTransmission() {
        this.speak("正在开启断点续传");
        String aofpath = ResourcePathRecorder.getAofDirPath();

        File file = new File(aofpath);

        File[] files = file.listFiles(f -> !f.getName().startsWith(ResourcePathRecorder.id2pathFileName));

        String resourceId = files[0].getName().substring(0,files[0].getName().lastIndexOf("-"));

        this.speak("正在向注册中心请求。。。");
        List<NodeInfo> resouceHolders = registryClient.getResouceHolders(resourceId);
        if (resouceHolders == null ||resouceHolders.size() == 0) {
            this.speak("目前集群中已不存在资源" + resourceId);
            rmLogAndResource();
            return;
        }

        this.speak("共有" + resouceHolders.size() +  "个节点拥有此资源");

        LoadBalance balance = ExtensionLoader.getExtension(LoadBalance.class);

        // 只会少不会多
        balance.select(resouceHolders, files.length);

        ProgressBar progressBar = new ProgressBar();
        progressBar.setSpeaker(this);

        String path = this.transportClient.resumeTransmission(resouceHolders, files, progressBar);
        this.speak("下载完毕！");

        this.speak("正在注册服务...");
        // down
        registLocal(resourceId,path);

        if (servicing) {
            registToRegistry(resourceId);
        }

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

    private void initRegistry() {
        String[] hosts = hostString.split(":");

        registryClient = ExtensionLoader.getExtension(RegistryClient.class);

        registryClient.connect(hosts[0],Integer.valueOf(hosts[1]));
    }

    private void initID2Path() {
        File file = new File(aofPath + "/" + ResourcePathRecorder.id2pathFileName);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        List<String> list = FileUtil.readList(file);
        // key%value
        for (String s : list) {
            if (s.isBlank()) {
                continue;
            }

            String[] kv = s.split("%");

            id2path.put(kv[0],kv[1]);
        }
    }

    public List<String> getLocalResources() {
        List<String> list = new ArrayList<>();
        for (String s : id2path.keySet()) {
            list.add(s);
        }

        return list;
    }
}
