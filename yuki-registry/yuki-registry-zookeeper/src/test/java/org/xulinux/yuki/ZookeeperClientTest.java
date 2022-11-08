package org.xulinux.yuki;

import org.junit.Test;
import org.xulinux.yuki.registry.NodeInfo;
import org.xulinux.yuki.registry.zookeeper.ZookeeperClient;

import java.util.concurrent.CountDownLatch;

/**
 * //TODO add class commment here
 *
 * @Author wfh
 * @Date 2022/10/9 下午6:30
 */
public class ZookeeperClientTest {
    @Test
    public void registTest() {
        ZookeeperClient zookeeperClient = new ZookeeperClient();

        zookeeperClient.connect();

//        zookeeperClient.registerResources("金刚大战蝙蝠侠",new NodeInfo("127.0.0.1:8080:20:32"));
//        System.out.println(zookeeperClient.getResouceHolders("金刚大战蝙蝠侠"));

        try {
            new CountDownLatch(1).await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            zookeeperClient.destry();
        }
    }
}
// close 会断开连接？