package org.xulinux.yuki.registry;

/**
 * //TODO add class commment here
 *
 * @Author wfh
 * @Date 2022/10/8 下午8:01
 */
public class NodeInfo  {
    private String ip;
    private int port;

    // 最大服务多少个
    private int maxServicing;

    // 现在服务了多少个
    private int nowServicing;

    public NodeInfo(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public NodeInfo() {
    }

    public NodeInfo(String nodeString) {
        String[] args = nodeString.split(":");
        this.ip = args[0];
        this.port = Integer.valueOf(args[1]);
        this.maxServicing = Integer.valueOf(args[2]);
        this.nowServicing = Integer.valueOf(args[3]);
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setMaxServicing(int maxServicing) {
        this.maxServicing = maxServicing;
    }

    public void setNowServicing(int nowServicing) {
        this.nowServicing = nowServicing;
    }

    public int getMaxServicing() {
        return maxServicing;
    }

    public int getNowServicing() {
        return nowServicing;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return ip  + ":" + port + ":" + maxServicing + ":" + nowServicing;
    }
}
