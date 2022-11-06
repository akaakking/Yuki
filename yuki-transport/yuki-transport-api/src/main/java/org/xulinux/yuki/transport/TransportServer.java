package org.xulinux.yuki.transport;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * //TODO add interface commment here
 *
 * @Author wfh
 * @Date 2022/10/11 下午2:50
 */
public interface TransportServer {
    void start();
    void terminal();
    AtomicInteger transporting();
    void setPort(int port);
}
