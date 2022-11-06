package org.xulinux.yuki;

import org.junit.Test;
import org.xulinux.yuki.common.spi.ExtensionLoader;
import org.xulinux.yuki.transport.TransportClient;

/**
 * //TODO add class commment here
 *
 * @Author wfh
 * @Date 2022/11/6 下午6:38
 */
public class SPI {
    @Test
    public void fds() {
        ExtensionLoader.getExtension(TransportClient.class);
    }
}
