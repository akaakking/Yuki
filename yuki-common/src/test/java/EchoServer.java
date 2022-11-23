import me.tongfei.progressbar.ProgressBar;
import org.xulinux.yuki.common.fileUtil.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * //TODO add class commment here
 *
 * @Author wfh
 * @Date 2022/10/13 下午8:20
 */
public class EchoServer {
    public static void main(String[] args) throws IOException {
        File file = new File("/home/wfh/123.txt");
        file.createNewFile();
        FileUtil.writeLine(file,"fsdfd");
        FileUtil.writeLine(file,"fdsfsdfsdfsdfds");

    }
}
