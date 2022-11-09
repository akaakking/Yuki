package org.xulinux.yuki.common.recorder;

import org.xulinux.yuki.common.fileUtil.FileSectionInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 首先构思使用场景
 * 1. 首先肯定是线程间可以共享的
 * 2.
 *
 *
 * @Author wfh
 * @Date 2022/11/8 下午9:33
 */
public class Recorder {
    private List<FileSectionInfo> sectionInfos;

    private int currentFileIndex;

    public Recorder(List<FileSectionInfo> sectionInfos) {
        this.sectionInfos = new ArrayList<>();
    }

    public void setFile(FileSectionInfo file) {

    }
}
