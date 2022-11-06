package org.xulinux.yuki.common.spi;

import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * //TODO add class commment here
 *
 * @Author wfh
 * @Date 2022/11/6 下午12:12
 */
public class ExtensionLoader {
    private static final ConcurrentHashMap<Class, Object> cachedInstance = new ConcurrentHashMap<>();

    public static <T> T  getExtension(Class<T> clazz) {
        Object instance = cachedInstance.get(clazz);

        if (instance != null) {
            return (T)instance;
        }

        ServiceLoader<?> serviceLoader = ServiceLoader.load(clazz);

        // 因为基本上就是第一个了
        Object o = serviceLoader.findFirst().get();

        cachedInstance.putIfAbsent(clazz,o);

        return (T)cachedInstance.get(clazz);
    }
}
