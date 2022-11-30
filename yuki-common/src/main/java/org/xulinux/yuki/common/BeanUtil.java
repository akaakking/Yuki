package org.xulinux.yuki.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * //TODO add class commment here
 *
 * @Author wfh
 * @Date 2022/11/30 下午1:30
 */
public class BeanUtil {
    private static Gson gson = new Gson();

    public static Gson getGson() {
        return gson;
    }
}
