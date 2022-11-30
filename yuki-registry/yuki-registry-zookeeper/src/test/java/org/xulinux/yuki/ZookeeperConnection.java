package org.xulinux.yuki;

import org.apache.zookeeper.*;
import org.junit.Test;
import org.xulinux.yuki.common.NodeInfo;
import org.xulinux.yuki.registry.DefaultLoadBalance;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * //TODO add class commment here
 *
 * @Author wfh
 * @Date 2022/9/30 下午5:52
 */
public class ZookeeperConnection {
    ZooKeeper zk;

    public ZooKeeper connect() throws IOException, InterruptedException {
        zk = new ZooKeeper("127.0.0.1:2181", 5000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("TODO 节点发生改动");
                System.out.println(event);
            }
        });


        return zk;
    }


    @Test
    public void creat() {
        try {
            ZooKeeper zk = connect();
            zk.create("/zkTest", "127.0.0.1:8080".getBytes(StandardCharsets.UTF_8), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.PERSISTENT);
            System.out.println(zk.exists("/zkTest", false).getVersion());
            zk.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void stream() {
        NodeInfo nodeInfo = new NodeInfo();
        nodeInfo.setMaxServicing(10);
        nodeInfo.setNowServicing(7);
        NodeInfo nodeInfo1 = new NodeInfo();
        nodeInfo1.setMaxServicing(10);
        nodeInfo1.setNowServicing(6);
        NodeInfo nodeInfo2 = new NodeInfo();
        nodeInfo2.setMaxServicing(10);
        nodeInfo2.setNowServicing(5);
        NodeInfo nodeInfo3 = new NodeInfo();
        nodeInfo3.setMaxServicing(10);
        nodeInfo3.setNowServicing(4);
        NodeInfo nodeInfo4 = new NodeInfo();
        nodeInfo4.setMaxServicing(10);
        nodeInfo4.setNowServicing(3);
        NodeInfo nodeInfo5 = new NodeInfo();
        nodeInfo5.setMaxServicing(10);
        nodeInfo5.setNowServicing(111);

        List<NodeInfo> nodeInfos  = Arrays.asList(nodeInfo,nodeInfo2,nodeInfo3,nodeInfo5,nodeInfo1,nodeInfo4);

        System.out.println(nodeInfos);
        DefaultLoadBalance defaultLoadBalance  = new DefaultLoadBalance();
        System.out.println(defaultLoadBalance.select(nodeInfos,2));

    }

    @Test
    public void ch() {
        int a  = 10;
        int b = 3;

        float c = (float) b / a;

        System.out.println(c);
        System.out.println(c == 0);
    }

}
