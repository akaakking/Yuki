package org.xulinux.yuki.common.recorder;

import org.xulinux.yuki.common.fileUtil.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * //TODO add class commment here
 *
 * @Author wfh
 * @Date 2022/11/22 下午9:18
 */
public class ResourcePathRecorder {
    private static final String id2pathFileName = "id2path.log";

    private static final ConcurrentHashMap<String,String> id2path = new ConcurrentHashMap<>();

    private static String aofPath;

    public static void record(String resourceId,String resourcePath) {
        FileUtil.writeLine(aofPath + "/" + id2pathFileName,resourceId + "%" + resourcePath);

        id2path.put(resourceId,resourcePath);
    }

    public static void initMap() {
        File file = new File(aofPath + "/" + id2pathFileName);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        List<String> list = FileUtil.readList(file.getPath());
        // key%value
        for (String s : list) {
            String[] kv = s.split("%");

            id2path.put(kv[0],kv[1]);
        }
    }

    public static void setAofPath(String aofPath) {
        ResourcePathRecorder.aofPath = aofPath;
    }
}
