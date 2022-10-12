package org.xulinux.yuki.common.FileUtil;

/**
 * //TODO add class commment here
 *
 * @Author wfh
 * @Date 2022/10/12 上午11:51
 */
public class FileSectionInfo {
    private FileInfo fileInfo;
    private long Index;
    private int offset;

    public FileSectionInfo(FileInfo fileInfo, long index, int offset) {
        this.fileInfo = fileInfo;
        Index = index;
        this.offset = offset;
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

    public long getIndex() {
        return Index;
    }

    public int getOffset() {
        return offset;
    }
}

