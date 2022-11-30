package org.xulinux.yuki.common.fileUtil;

/**
 * //TODO add class commment here
 *
 * @Author wfh
 * @Date 2022/10/12 上午11:51
 */
public class FileSectionInfo {
    private FileInfo fileInfo;
    private long offset;
    private int length;

    public FileSectionInfo(FileInfo fileInfo,long offset,int length) {
        this.fileInfo = fileInfo;
        this.offset = offset;
        this.length = length;
    }

    public void setFileInfo(FileInfo fileInfo) {
        this.fileInfo = fileInfo;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public int getLength() {
        return length;
    }

    public long getOffset() {
        return offset;
    }

    public String getFileName() {
        return fileInfo.getName();
    }

    public long getFileSize() {
        return fileInfo.getSize();
    }

    public String getDirPath() {
        return fileInfo.getDirPath();
    }

    public FileInfo getFileInfo() {
        return fileInfo;
    }

    public int receiveAndGet(int received) {
        this.offset += received;
        this.length -= received;

        return this.length;
    }

    public void receive(int received) {
        this.offset += received;
        this.length -= received;
    }
}

