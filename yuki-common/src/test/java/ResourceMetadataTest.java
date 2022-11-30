import org.junit.Assert;
import org.junit.Test;
import org.xulinux.yuki.common.fileUtil.FileSectionInfo;
import org.xulinux.yuki.common.fileUtil.ResourceMetadata;

import java.io.File;
import java.util.List;

/**
 * //TODO add class commment here
 *
 * @Author wfh
 * @Date 2022/11/29 下午7:49
 */
public class ResourceMetadataTest {
    @Test
    public void constructor() {
        ResourceMetadata resourceMetadata = new ResourceMetadata("/home/wfh/Downloads/feifei");

        resourceMetadata.creatDir("/home/wfh/downDir");

        List<List<FileSectionInfo>> all = resourceMetadata.split(3);


        String fileName = "Netty源码剖析与应用 (刘耀林) (z-lib.org).pdf";

        String path = "/home/wfh/Downloads/feifei/" + fileName;

        File file = new File(path);

        long originSize = file.length();
        long cacuSize = 0;

        for (List<FileSectionInfo> fileSectionInfos : all) {
            for (FileSectionInfo fileSectionInfo : fileSectionInfos) {
                if (fileSectionInfo.getFileName().equals(fileName)) {
                    cacuSize += fileSectionInfo.getLength();
                }
            }
        }


        System.out.println(all.get(0).size());
        System.out.println(all.get(1).size());
        System.out.println(all.get(2).size());

        long one = 0;
        long two = 0;
        long three = 0;


        for (FileSectionInfo sectionInfo : all.get(0)) {
            one += sectionInfo.getLength();
        }

        for (FileSectionInfo sectionInfo : all.get(1)) {
            two += sectionInfo.getLength();
        }

        for (FileSectionInfo sectionInfo : all.get(2)) {
            three += sectionInfo.getLength();
        }

        System.out.println(one);
        System.out.println(two);
        System.out.println(three);

        Assert.assertEquals(originSize,cacuSize);
    }
}
