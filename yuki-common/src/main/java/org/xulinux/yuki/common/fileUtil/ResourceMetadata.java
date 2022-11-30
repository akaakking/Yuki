package org.xulinux.yuki.common.fileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 这是总的一个目录的整个信息
 *
 * @Author wfh
 * @Date 2022/10/12 上午11:38
 */
public class ResourceMetadata  {
    // 8 M
    public static int DEFAULT_MAX_SECTION_SIZE = 1 << 23;

    private List<FileInfo> files;
    private List<String> dirs;
    private List<List<FileSectionInfo>> sections;

    // resource baseDir在本机的绝对路径
    // 比如说 我们认为 /home/wfh/dubbo 是一个资源
    // 那么这个resourthpath实际上指的是？
    private transient String resourceParentPath;

    // 只可传输目录不可传输文件
    public ResourceMetadata(String resourcepath) {
        // /home/wfh/dubbo
        // /home/wfh
        this.dirs = new ArrayList<>();
        this.files = new ArrayList<>();
        this.resourceParentPath = resourcepath.substring(0, resourcepath.lastIndexOf("/"));
        File file = new File(resourcepath);

        visit(file);
    }

    private void visit(File file) {
        if (file.isFile()) {
            // 是一个file
            FileInfo fileInfo = new FileInfo();
            fileInfo.setSize(file.length());
            fileInfo.setName(file.getName());
            fileInfo.setDirPath(file.getParent().replace(this.resourceParentPath,""));
            files.add(fileInfo);
            return;
        }

        // 是一个文件夹
        // /dubbo
        dirs.add(file.getAbsolutePath().replace(this.resourceParentPath,""));

        for (File f : file.listFiles()) {
            visit(f);
        }
    }

    private void creatDir() {
        for (String dir : dirs) {
            File file = new File(this.resourceParentPath + dir);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
    }

    public void creatDir(String resourceParentPath) {
        this.resourceParentPath = resourceParentPath;
        creatDir();
    }



    public List<List<FileSectionInfo>> split(int holderCount) {
        sections = new ArrayList<>(holderCount);

        for (int i = 0; i < holderCount; i++) {
            sections.add(new ArrayList<>());
        }

        int index = 0;

        for (FileInfo file : files) {
            long unallocated = file.getSize();

            long offset = 0;

            while (unallocated != 0) {
                int alloc = unallocated > DEFAULT_MAX_SECTION_SIZE
                        ? DEFAULT_MAX_SECTION_SIZE
                        : (int) unallocated;

                sections.get(index++ % holderCount).add(new FileSectionInfo(file,offset,alloc));
                unallocated -= alloc;
                offset += alloc;
            }
        }

        return sections;
    }
}

// 先不考虑做优化。 就是删掉前缀这些。
