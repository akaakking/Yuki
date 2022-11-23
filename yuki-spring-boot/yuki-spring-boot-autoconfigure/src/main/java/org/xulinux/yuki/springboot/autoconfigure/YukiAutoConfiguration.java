package org.xulinux.yuki.springboot.autoconfigure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.xulinux.yuki.common.recorder.ResourcePathRecorder;
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
@Configuration
@ConditionalOnClass(NodeServer.class)
@EnableConfigurationProperties(YukiProperties.class)
public class YukiAutoConfiguration {

    @Autowired
    private YukiProperties yukiProperties;

    @Bean
    @ConditionalOnMissingBean(NodeServer.class)
    @ConditionalOnProperty(prefix = "yuki",value = "enabled", havingValue = "true",matchIfMissing = true)
    public NodeServer nodeServer() {
        NodeServer nodeServer = new NodeServer();

        // zk
        YukiProperties.Registry registry = yukiProperties.getRegistry();
        String registryIp = registry.getIp();
        int registryPort = registry.getPort();
        nodeServer.setHostString(registryIp + ":" + registryPort);

        // nodeinfo
        int nodePort = yukiProperties.getPort();
        int maxServicing = yukiProperties.getMaxServicing();
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
        String aofPath = yukiProperties.getAofPath();
        nodeServer.setAofPath(aofPath);
        ResourcePathRecorder.setAofDirPath(aofPath);

        return nodeServer;
    }

    public void setYukiProperties(YukiProperties yukiProperties) {
        this.yukiProperties = yukiProperties;
    }

    public YukiProperties getYukiProperties() {
        return yukiProperties;
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
