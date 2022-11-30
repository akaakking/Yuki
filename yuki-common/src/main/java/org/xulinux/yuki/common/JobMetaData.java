package org.xulinux.yuki.common;

import org.xulinux.yuki.common.fileUtil.FileSectionInfo;

import java.util.List;

/**
 * //TODO add class commment here
 *
 * @Author wfh
 * @Date 2022/11/26 下午11:14
 */
public class JobMetaData {
    private String resourceId;
    private String downDir;
    private List<FileSectionInfo> sectionInfos;
    private String hostString;


    public JobMetaData() {
    }

    // 得到一个新的
    public JobMetaData clone(List<FileSectionInfo> sectionInfos, String hostString) {
        JobMetaData jobMetaData = new JobMetaData(resourceId,downDir);
        jobMetaData.setSectionInfos(sectionInfos);
        jobMetaData.setHostString(hostString);

        return jobMetaData;
    }

    public void setHostString(String hostString) {
        this.hostString = hostString;
    }

    public String getHostString() {
        return hostString;
    }

    public JobMetaData(String resourceId, String downDir) {
        this.resourceId = resourceId;
        this.downDir = downDir;
    }

    public void setSectionInfos(List<FileSectionInfo> sectionInfos) {
        this.sectionInfos = sectionInfos;
    }

    public List<FileSectionInfo> getSectionInfos() {
        return sectionInfos;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getDownDir() {
        return downDir;
    }

    public void setDownDir(String downDir) {
        this.downDir = downDir;
    }
}
