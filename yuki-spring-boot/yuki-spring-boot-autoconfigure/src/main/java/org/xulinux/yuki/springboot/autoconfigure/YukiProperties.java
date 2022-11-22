package org.xulinux.yuki.springboot.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * //TODO add class commment here
 *
 * @Author wfh
 * @Date 2022/11/5 下午4:41
 */
@ConfigurationProperties(prefix = "yuki")
public class YukiProperties {
    private int port;

    private String aofPath;

    private int maxServicing;

    private Registry registry;

    public String getAofPath() {
        return aofPath;
    }

    public void setAofPath(String aofPath) {
        this.aofPath = aofPath;
    }

    public void setMaxServicing(int maxServicing) {
        this.maxServicing = maxServicing;
    }

    public int getMaxServicing() {
        return maxServicing;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public static class Registry {
        private String ip;
        private int port;

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }
    }

    public Registry getRegistry() {
        return registry;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    @Override
    public String toString() {
        return "YukiProperties{" +
                "port=" + port +
                ", aofPath='" + aofPath + '\'' +
                ", maxServicing=" + maxServicing +
                ", registry=" + registry +
                '}';
    }
}
