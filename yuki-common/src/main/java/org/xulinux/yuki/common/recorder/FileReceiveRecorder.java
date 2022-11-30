package org.xulinux.yuki.common.recorder;

import org.xulinux.yuki.common.BeanUtil;
import org.xulinux.yuki.common.JobMetaData;
import org.xulinux.yuki.common.fileUtil.FileSectionInfo;
import org.xulinux.yuki.common.fileUtil.FileUtil;

import java.io.File;
import java.io.IOException;
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

    private int count;
    private File aofFile;
    private FileSectionInfo curSection;
    private int currentSectionIndex;
    private JobMetaData jobMetaData;

    /**
     * 负责从文件中恢复
     * @param path
     */
    public FileReceiveRecorder(String path) {
        this.totalSize = new AtomicLong();
        this.aofFile = new File(path);
        
        List<String> logs = FileUtil.readList(path);

        this.jobMetaData = BeanUtil.getGson().fromJson(logs.get(0),JobMetaData.class);

        rollBack(jobMetaData.getSectionInfos(),logs);
    }



    private void rollBack(List<FileSectionInfo> sectionInfos, List<String> logs) {
        long transfered = 0;

        for (int i = 1; i < logs.size(); i++) {
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

        clear();
        calculateTotalSize(sectionInfos);
    }

    public FileReceiveRecorder(JobMetaData jobMetaData) {
        this.jobMetaData = jobMetaData;
        this.totalSize = new AtomicLong();
        this.currentSectionIndex = 0;
        this.curSection = jobMetaData.getSectionInfos().get(currentSectionIndex);

        calculateTotalSize(jobMetaData.getSectionInfos());

        String path = ResourcePathRecorder.getAofDirPath() + "/" + jobMetaData.getResourceId() + "-" + jobMetaData.getHostString();

        this.aofFile = new File(path);

        if (!aofFile.exists()) {
            try {
                aofFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        persistence(jobMetaData);
    }

    public JobMetaData getJobMetaData() {
        return jobMetaData;
    }

    private void calculateTotalSize(List<FileSectionInfo> sectionInfos) {
        for (FileSectionInfo sectionInfo : sectionInfos) {
            totalSize.addAndGet(sectionInfo.getLength());
        }
    }

    // 写满4m将当前状态做一个snapshot
    // 就要看允许丢失多少了
    public void record(int received) {
        int remainingLength = this.curSection.receiveAndGet(received);
        long remainSize = this.totalSize.addAndGet(received * -1);

        count += received;

        // 4M
        if (count > (2 << 22))  {
            persistence(count);
            count = 0;
        }

        if (remainingLength == 0) {
            this.curSection = this.jobMetaData.getSectionInfos().get(++currentSectionIndex);
        }

        if (remainSize == 0) {
            // 传输完成，删除aof
            this.aofFile.delete();
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
    private void persistence(JobMetaData jobMetaData) {
        String str = BeanUtil.getGson().toJson(jobMetaData);
        FileUtil.writeLine(aofFile,str);
    }

    public AtomicLong getTotalSize() {
        return totalSize;
    }


    // todo test
    public void clear() {
        int  i = 0;
        for (; i < this.jobMetaData.getSectionInfos().size(); i++) {
            if (this.jobMetaData.getSectionInfos().get(i).getLength() != 0) {
                break;
            }
        }

        this.jobMetaData.getSectionInfos().subList(0,i).clear();
        this.currentSectionIndex = 0;
        this.curSection = this.jobMetaData.getSectionInfos().get(currentSectionIndex);
    }
}