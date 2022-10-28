package org.xulinux.yuki.springboot.autoconfigure;

import jdk.jshell.Snippet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.w3c.dom.Node;
import org.xulinux.yuki.nodeServer.NodeServer;
import org.xulinux.yuki.registry.NodeInfo;

import java.net.Inet4Address;
import java.net.UnknownHostException;

/**
 * //TODO add class commment here
 *
 * @Author wfh
 * @Date 2022/10/28 上午10:21
 */
public class YukiAutoConfiguration {
    @Autowired
    private Environment environment;

    @Bean
    public NodeServer getNodeServer() {
        NodeServer nodeServer = new NodeServer();

        // zk
        String zkIp = environment.getProperty("yuki.zkIp");
        int zkPort = Integer.parseInt(environment.getProperty("yuki.zkPort"));
        nodeServer.setZkIp(zkIp);
        nodeServer.setZkPort(zkPort);

        // nodeinfo
        int nodePort = Integer.parseInt(environment.getProperty("yuki.nodeinfo.port"));
        int maxServicing = Integer.parseInt(environment.getProperty("yuki.nodeinfo.maxServicing"));
        String ip = null;
        try {
            ip = Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            // TODO 日志
            System.exit(0);
        }
        NodeInfo nodeInfo = new NodeInfo();
        nodeInfo.setIp(ip);
        nodeInfo.setPort(nodePort);
        nodeInfo.setMaxServicing(maxServicing);
        nodeServer.setNodeInfo(nodeInfo);

        // 持久化
        String aofPath = environment.getProperty("yuki.aofPath");
        nodeServer.setAofPath(aofPath);

        return nodeServer;
    }

    /**
     * 现在的话
     * 没有解决的问题就是缺乏一个存储，因为开机启动后不知道自己拥有资源的一个名单
     * 两个思路
     * 1. 存储在zk
     * 2. 存储在本机上线后再次进行注册
     * 3. 存储在本机吧
     */
}
