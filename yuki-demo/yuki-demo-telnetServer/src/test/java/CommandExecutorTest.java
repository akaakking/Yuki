import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 *
 *
 */
public class CommandExecutorTest {
    public static void main(String[] ars) {
//        // 线程不安全？
//        RandomAccessFile randomAccessFile = new RandomAccessFile("/home/wfh/ty/11.22日.md","rw");
//        randomAccessFile.write("fdsfds".getBytes(StandardCharsets.UTF_8));

        Executor executor = Executors.newCachedThreadPool();

        BlockingQueue<Integer> blockingQueue = new LinkedBlockingQueue<>();

        blockingQueue.add(0);
        blockingQueue.add(5);

        Runnable runnable = () -> {
            RandomAccessFile raf = null;
            try {
                raf = new RandomAccessFile("/home/wfh/ty/11.22日.md", "rw");

                raf.seek(blockingQueue.poll());
                for (int i = 0; i < 5; i++) {
                    Thread.sleep(500);
                    raf.write((byte) i);
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    raf.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        executor.execute(runnable);
        executor.execute(runnable);
    }

    @Test
    public void downloadtest() {
        String message = "download makabka to mydir";
        String resourceId = message.substring("download ".length(), message.lastIndexOf("to")).trim();
        String downDir = message.substring(message.lastIndexOf("to") + 2).trim();

        System.out.println(resourceId);
        System.out.println(downDir);
    }
}
