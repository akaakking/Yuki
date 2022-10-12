package org.xulinux.yuki.common.FileUtil;

import java.io.File;
import java.util.Iterator;
import java.util.List;

/**
 * 这是总的一个目录的整个信息
 *
 * @Author wfh
 * @Date 2022/10/12 上午11:38
 */
public class ResourceMetadata implements Iterable<FileInfo>{
    private List<FileInfo> files;
    private transient String resourcePath;

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


    @Override
    public Iterator<FileInfo> iterator() {
        return files.iterator();
    }
}

// 先不考虑做优化。 就是删掉前缀这些。
