package org.xulinux.yuki.transport;

import org.xulinux.yuki.common.fileUtil.FileSectionInfo;
import org.xulinux.yuki.common.fileUtil.ResourceMetadata;

import java.util.List;

/**
 * 封装的消息类型
 *
 * @Author wfh
 * @Date 2022/10/14 上午9:07
 */
public class Message {
    private Type type;
    private String resourceId;
    private ResourceMetadata metadata;
    private List<FileSectionInfo> sectionInfos;
    private Integer sectionIndex;


    public void setSectionIndex(Integer sectionIndex) {
        this.sectionIndex = sectionIndex;
    }

    public Integer getSectionIndex() {
        return sectionIndex;
    }


    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public ResourceMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(ResourceMetadata metadata) {
        this.metadata = metadata;
    }

    public List<FileSectionInfo> getSectionInfos() {
        return sectionInfos;
    }

    public void setSectionInfos(List<FileSectionInfo> sectionInfos) {
        this.sectionInfos = sectionInfos;
    }

    public enum Type {
        /**
         * 请求metadata
         */
        METADATA_REQUEST,

        /**
         * 返回metadata
         */
        METADATA_RESPONSE,

        /**
         * 接收filesections的分配，如果这个分配有序那岂不是可以，服务器每次发送前不是可以不发文件的头信息。
         */
        FILE_SECTION_ASSIGN,

        File_HEAD_INFO
    }
}

