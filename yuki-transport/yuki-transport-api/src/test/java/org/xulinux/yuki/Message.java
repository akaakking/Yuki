package org.xulinux.yuki;

/**
 * //TODO add class commment here
 *
 * @Author wfh
 * @Date 2022/10/14 下午2:54
 */
public class Message {
    private long fileSize;
    private String fileName;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public long getFileSize() {
        return fileSize;
    }
}
