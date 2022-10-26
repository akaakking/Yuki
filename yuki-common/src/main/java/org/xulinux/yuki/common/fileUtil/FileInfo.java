package org.xulinux.yuki.common.fileUtil;

/**
 * //TODO add class commment here
 *
 * @Author wfh
 * @Date 2022/10/12 上午11:44
 */
public class FileInfo {
    private long size;
    private String name;
    private String dirPath;
    private boolean directory;

    public void setSize(long size) {
        this.size = size;
    }

    public void setDirectory(boolean directory) {
        this.directory = directory;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDirPath(String dirPath) {
        this.dirPath = dirPath;
    }

    public boolean isDirectory() {
        return directory;
    }

    public long getSize() {
        return size;
    }

    public String getName() {
        return name;
    }

    public String getDirPath() {
        return dirPath;
    }
}
// 空目录会消失的 要空目录有何意义。