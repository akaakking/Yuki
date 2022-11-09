package org.xulinux.yuki.common.recorder;

import org.xulinux.yuki.common.fileUtil.FileSectionInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO 持久化
 *
 * @Author wfh
 * @Date 2022/11/8 下午9:33
 */
public class Recorder {

    private List<FileSectionInfo> sectionInfos;

    private FileSectionInfo fileSectionInfo;

    public Recorder(List<FileSectionInfo> sectionInfos) {
        this.sectionInfos = new ArrayList<>();
    }

    public void setFile(int currentFileIndex) {
        fileSectionInfo = sectionInfos.get(currentFileIndex);
    }

    public void record(int received) {
        this.fileSectionInfo.receive(received);
    }
}
