package org.xulinux.yuki.common.recorder;

import com.alibaba.fastjson.JSON;
import org.xulinux.yuki.common.fileUtil.FileSectionInfo;
import org.xulinux.yuki.common.fileUtil.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 这个主要就是针对一项任务的记录。
 *
 * @Author wfh
 * @Date 2022/11/8 下午9:33
 */
public class FileReceiveRecorder {
    private AtomicLong totalSize;

    private int numOfNode;

    private int count;

    private String downDir;

    private File aofFile;

    private List<FileSectionInfo> sectionInfos;

    private FileSectionInfo fileSectionInfo;

    /**
     * 负责从文件中恢复
     * @param path
     */
    public FileReceiveRecorder(String path) {
        this.aofFile = new File(path);
        List<String> logs = FileUtil.readList(path);
        this.downDir = logs.get(0);
        List<FileSectionInfo> sectionInfos = (List<FileSectionInfo>) JSON.parse(logs.get(1));

        rollBack(sectionInfos,logs);
    }

    private void rollBack(List<FileSectionInfo> sectionInfos, List<String> logs) {
        long transfered = 0;

        for (int i = 2; i < logs.size(); i++) {
            transfered += Integer.valueOf(logs.get(i));
        }

        for (int i = 0; i < sectionInfos.size(); i++) {
            FileSectionInfo fileSectionInfo = sectionInfos.get(i);

            int sectionlength = fileSectionInfo.getLength();
            if (sectionlength < transfered) {
                fileSectionInfo.receive(sectionlength);
                transfered -= sectionlength;
            } else {
                fileSectionInfo.receive((int)transfered);
            }
        }

        this.sectionInfos = sectionInfos;
        clear();
        calculateTotalSize(sectionInfos);
    }

    public FileReceiveRecorder(List<FileSectionInfo> sectionInfos,String resourceId, int numOfNode,String downDir) {
        this.downDir = downDir;
        this.numOfNode = numOfNode;
        this.sectionInfos = new ArrayList<>();
        this.totalSize = new AtomicLong();

        calculateTotalSize(sectionInfos);

        String path = ResourcePathRecorder.getAofDirPath()  + "/" + resourceId  + "-" + numOfNode;

        this.aofFile = new File(path);
        if (!aofFile.exists()) {
            try {
                aofFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        persistence(sectionInfos);
    }

    public void setFile(int currentFileIndex) {
        fileSectionInfo = sectionInfos.get(currentFileIndex);
    }

    private void calculateTotalSize(List<FileSectionInfo> sectionInfos) {
        for (FileSectionInfo sectionInfo : sectionInfos) {
            totalSize.addAndGet(sectionInfo.getLength());
        }
    }

    // 写满4m将当前状态做一个snapshot
    // 就要看允许丢失多少了
    public void record(int received) {
        this.fileSectionInfo.receive(received);
        this.totalSize.addAndGet(received * -1);

        count += received;

        // 4M
        if (count > (2 << 22))  {
            persistence(count);
            count = 0;
        }
    }

    /**
     * basedir
     * resourceid
     * List<FileSectionInfo>
     * count
     *
     * @param count
     */

    // 持久化传输了多少字节
    private void persistence(int count) {
        FileUtil.writeLine(aofFile,count);
    }

    // 持久化整个列表
    private void persistence(List<FileSectionInfo> sectionInfos) {
        FileUtil.writeLine(aofFile,downDir);
        String str = JSON.toJSONString(sectionInfos);
        FileUtil.writeLine(aofFile,str);
    }

    public AtomicLong getTotalSize() {
        return totalSize;
    }

    public int getNumOfNode() {
        return numOfNode;
    }

    public void clear() {
        int  i = 0;
        for (; i < sectionInfos.size(); i++) {
            if (sectionInfos.get(i).getLength() != 0) {
                break;
            }
        }

        sectionInfos.subList(0,i).clear();
    }
}