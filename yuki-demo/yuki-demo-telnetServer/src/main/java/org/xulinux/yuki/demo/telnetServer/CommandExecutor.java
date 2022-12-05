package org.xulinux.yuki.demo.telnetServer;

import io.netty.channel.ChannelHandlerContext;
import org.xulinux.yuki.nodeServer.NodeServer;

import java.util.List;

/**
 *
 * @Author wfh
 * @Date 2022/10/30 下午12:17
 */
public class CommandExecutor  {

    public static final String DOWNLOAD = "download";
    public static final String SERVICE = "service";
    public static final String SHOW = "show";

    public static final String REGIST = "regist";

    /**
     * 关闭客户端连接
     */
    public static final String EXIT = "exit";

    private static NodeServer nodeServer;

    private static TelnetNettyServer telnetNettyServer;
    private static ChannelHandlerContext ctx;

    // 这个东西太丑了，可以改一下但没必要
    public static void execute(String message) {
        if (message.startsWith(DOWNLOAD)) {
            download(message);
        } else if (message.startsWith(REGIST)) {
            regist(message);
        } else if (message.startsWith(SERVICE)) {
            service(message);
        } else if (message.startsWith(SHOW)) {
            show(message);
        } else  if (message.startsWith("shutdown")){
            shut(message);
        } else if(message.startsWith("search")) {
            search(message);
        } else if (message.equalsIgnoreCase(EXIT) || message.equalsIgnoreCase("x")) {
            try {
                ctx.channel().close().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if (message.equalsIgnoreCase("y") || message.equalsIgnoreCase("n")) {
            dealDowntime(message);
        } else {
           withoutCommand(message);
        }
    }

    public static void setTelnetNettyServer(TelnetNettyServer telnetNettyServer) {
        CommandExecutor.telnetNettyServer = telnetNettyServer;
    }


    // todo shutdown gracefully
    private static void shut(String message) {
        if (message.length() > "shutdown".length()) {
            nodeServer.shutdown(true);
        } else {
            nodeServer.shutdown(false);
        }

        telnetNettyServer.shutdown();
    }

    // search *
    private static void search(String message) {
        List<String> list = nodeServer.searchResources(message.substring(6).trim());
        for (String s : list) {
            nodeServer.speak(s);
        }
    }


    // regist resourceId path
    private static void regist(String message) {
        String[] resourceIDAndPath = message.substring("regist ".length()).trim().split(" ");
        nodeServer.registerResourth(resourceIDAndPath[0],resourceIDAndPath[1]);
    }

    public static void dealDowntime(String message) {
        if (!nodeServer.hasDowntime()) {
            withoutCommand(message);
        }

        if (message.equalsIgnoreCase("y")) {
            // 断点续传
            nodeServer.resumeTransmission();
        } else {
            // 删除log
            nodeServer.rmLogAndResource();
        }
    }



    // 拉的这个操作很重（注册中心带宽，网络传输延迟），todo 拉去之后缓存起来。
    private static void show(String message) {
        if (message.equalsIgnoreCase("show local")) {
            showLocalResource();
        } else if (message.equalsIgnoreCase("show remote")){
            showRemote();
        } else {
            withoutCommand(message);
        }
    }


    // 以下两个其实就是一个问题就是如何描述资源结构的问题，以及资源的存储问题。

    // todo remote
    private static void showRemote() {
        search("search *");
    }

    // TODO 存储在哪里？ hashmap的初始化
    private static void showLocalResource() {
        for (String localResource : nodeServer.getLocalResources()) {
            nodeServer.speak(localResource);
        }
    }

    // download resouceid to dir
    private static void download(String message) {
        String resourceId = message.substring("download ".length(), message.lastIndexOf("to")).trim();
        String downDir = message.substring(message.lastIndexOf("to") + 2).trim();

        nodeServer.download(resourceId,downDir);
    }

    private static void withoutCommand(String message) {
        nodeServer.speak(CommandConstant.NO_COMMAND + message);
    }

    public static void setCtx(ChannelHandlerContext ctx) {
        CommandExecutor.ctx = ctx;
    }

    private static void service(String message) {
        if (message.equalsIgnoreCase("service status")) {
            serviceStatus();
        } else if (message.equalsIgnoreCase("service stop")) {
            serviceStop();
        } else if (message.equalsIgnoreCase("service start")) {
            serviceStart();
        } else {
            withoutCommand(message);
        }
    }

    private static void serviceStatus() {
        if (!nodeServer.isServicing()) {
            nodeServer.speak("Service is not turned on");
            return;
        }

        nodeServer.speak("There are currently " + nodeServer.getTransCount() +
                " services tasks running");
    }

    private static void serviceStart() {
        nodeServer.exportService();
    }

    private static void serviceStop() {
        nodeServer.terminal();
    }

    public static void setNodeServer(NodeServer nodeServer) {
        CommandExecutor.nodeServer = nodeServer;
    }
}

//
//         * 1. 暴露服务
//         * 2. 查询当前节点所具有的资源列表
//         * 3. 根据resourceid下载资源
//         * 5. 查看正在向外传输的资源