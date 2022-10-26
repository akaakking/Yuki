package org.xulinux.yuki.common.fileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 这是总的一个目录的整个信息
 *
 * @Author wfh
 * @Date 2022/10/12 上午11:38
 */
public class ResourceMetadata {
    // 8 M
    public static int DEFAULT_MAX_SECTION_SIZE = 1 << 23;

    private List<FileInfo> files;
    private List<List<FileSectionInfo>> sections;
    private transient String resourcePath;
    private int holderCount;

    public void setHolderCount(int holderCount) {
        this.holderCount = holderCount;
    }

    public void setFiles(List<FileInfo> files) {
        this.files = files;
    }

    public List<FileInfo> getFiles() {
        return files;
    }

    public List<List<FileSectionInfo>> getSections() {
        return sections;
    }

    public void setSections(List<List<FileSectionInfo>> sections) {
        this.sections = sections;
    }

    public List<List<FileSectionInfo>> split() {
        sections = new ArrayList<>(holderCount);

        for (int i = 0; i < holderCount; i++) {
            sections.add(new ArrayList<>());
        }

        for (FileInfo file : files) {
            if (file.isDirectory()) {
                continue;
            }

            long unallocated = file.getSize();
            int  index = 0;
            int own = 0;

            while (unallocated > 0) {
                int offset;

                if (unallocated > DEFAULT_MAX_SECTION_SIZE) {
                    offset = DEFAULT_MAX_SECTION_SIZE;
                    unallocated -= DEFAULT_MAX_SECTION_SIZE;
                } else {
                    offset = (int)unallocated;
                    unallocated = 0;
                }

                FileSectionInfo fsi = new FileSectionInfo(file,index,offset);
                sections.get(own++ % holderCount).add(fsi);
            }
        }

        return sections;
    }

    public ResourceMetadata() {
    }

    // 只可传输目录不可传输文件
    public ResourceMetadata(String resourcepath) {
        this.resourcePath = resourcepath;
        File file = new File(resourcepath);

        visit(file);
    }

    private void visit(File file) {
        if (file.isFile()) {
            FileInfo fileInfo = new FileInfo();
            fileInfo.setSize(file.length());
            fileInfo.setName(file.getName());
            fileInfo.setDirPath(file.getAbsolutePath().replace(resourcePath,""));
            fileInfo.setDirectory(false);
            files.add(fileInfo);
            return;
        }

        FileInfo dirInfo = new FileInfo();
        dirInfo.setDirectory(true);
        dirInfo.setDirPath(file.getAbsolutePath().replace(resourcePath,""));
        dirInfo.setName(file.getName());
        files.add(dirInfo);

        for (File f : file.listFiles()) {
            visit(f);
        }
    }
}

// 先不考虑做优化。 就是删掉前缀这些。
