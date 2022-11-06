package org.xulinux.yuki.common;

/**
 * //TODO add class commment here
 *
 * @Author wfh
 * @Date 2022/10/30 下午5:41
 */
public interface Speaker {
    void speak(String message);
    void addListenner(Listenner listenner);
    void removeListenner(Listenner listenner);
}
