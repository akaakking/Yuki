package org.xulinux.yuki.demo.telnetServer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.xulinux.yuki.nodeServer.NodeServer;
import org.xulinux.yuki.springboot.autoconfigure.YukiProperties;

import java.io.OutputStream;

/**
 * 因为是文件传输嘛，所以还得搭一个产品
 *
 * 主要就是以下这几个功能
 * 下载等其他操作全部阻塞进行
 *  也就是客户端操作全部阻塞。
 *
 * 1. 暴露服务
 * 2. 查询当前节点所具有的资源列表
 * 3. 根据resourceid下载资源
 * 5. 查看正在向外传输的资源
 *
 * @Author wfh
 * @Date 2022/10/28 上午11:29
 */
@SpringBootApplication
public class ServerApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ServerApplication.class, args);
//        System.out.println(context.getBean(YukiProperties.class));
//        context.getBean(NodeServer.class);
        Environment environment = context.getBean(Environment.class);
        System.out.println(environment.getProperty("yuki.telnet.port"));
        NodeServer nodeServer   = context.getBean(NodeServer.class);

        int port = Integer.parseInt(environment.getProperty("yuki.telnet.port"));

        TelnetNettyServer telnetNettyServer = new TelnetNettyServer(nodeServer);
        telnetNettyServer.start(port);
    }
}
