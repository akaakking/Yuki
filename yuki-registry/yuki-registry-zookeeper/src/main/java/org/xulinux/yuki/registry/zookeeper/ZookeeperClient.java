package org.xulinux.yuki.registry.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.xulinux.yuki.registry.NodeInfo;
import org.xulinux.yuki.registry.RegistryClient;

import java.util.ArrayList;
import java.util.List;

/**
 * //TODO add class commment here
 *
 * @Author wfh
 * @Date 2022/10/8 下午6:17
 */
public class ZookeeperClient implements RegistryClient {
    private int port;
    private String ip;
    // todo 考虑加锁的问题

    public static final String YUKI_PRE = "/yuki/";
    private CuratorFramework client;

    public ZookeeperClient() {
    }

    @Override
    public void connect() {
        //调用工厂类CuratorFrameworkFactory的静态newClient()方法
        //第一个参数：ZK的连接地址
        //第二个参数：重试策略
        String connectString = ip == null ? "127.0.0.1:2181" : ip + ":" + port;

        client = CuratorFrameworkFactory.newClient(
                connectString,
                new ExponentialBackoffRetry(1000, 3));
        client.start();
    }

    @Override
    public void registerResources(String resourceId, NodeInfo nodeInfo) {
        String path = buildNodePath(resourceId,nodeInfo);
        try {
            client.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<NodeInfo> getResouceHolders(String resourceId) {
        List<NodeInfo> holders = new ArrayList<>();
        try {
            List<String> list = client.getChildren().forPath(YUKI_PRE + resourceId);
            if (list != null) {
                for (String s : list) {
                    holders.add(new NodeInfo(s));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return holders;
    }

    private String buildNodePath(String resourceId, NodeInfo nodeInfo) {
        return YUKI_PRE + resourceId + "/" + nodeInfo;
    }

    @Override
    public void unRegisterResources(String resourceId,NodeInfo nodeInfo) {
        String path = buildNodePath(resourceId,nodeInfo);
        try {
            client.delete()
                    .forPath(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setRegistryHost(String ip, int port) {
        setIp(ip);
        setPort(port);
    }


    @Override
    public void destry() {
        client.close();
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
