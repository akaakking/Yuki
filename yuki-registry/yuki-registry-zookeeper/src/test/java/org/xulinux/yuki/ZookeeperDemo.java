package org.xulinux.yuki;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

/**
 * //TODO add class commment here
 *
 * @Author wfh
 * @Date 2022/9/30 下午3:34
 */
public class ZookeeperDemo {
    public static void main(String[] args) {
        //调用工厂类CuratorFrameworkFactory的静态newClient()方法
        //第一个参数：ZK的连接地址
        //第二个参数：重试策略
        CuratorFramework client = CuratorFrameworkFactory.newClient(
                "127.0.0.1:2181",
                new ExponentialBackoffRetry(1000, 3));

        String path = "/zkTest1/zkTest2/zk";
        client.start();

        try {
            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
