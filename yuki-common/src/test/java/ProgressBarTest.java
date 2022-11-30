import org.junit.Test;
import org.xulinux.yuki.common.ProgressBar;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * //TODO add class commment here
 *
 * @Author wfh
 * @Date 2022/11/28 上午10:36
 */
public class ProgressBarTest {

    public static void main(String[] args) {
        ProgressBar progressBar = new ProgressBar();


        Executor executor = Executors.newCachedThreadPool();

        Runnable run = new Runnable() {
            @Override
            public void run() {
                AtomicLong num = new AtomicLong(40);
                progressBar.add(num);
                for (;;) {
                    num.decrementAndGet();
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };

        executor.execute(run);
        executor.execute(run);

        progressBar.show();
    }



}
