import org.junit.Test;
import org.xulinux.yuki.common.ProgressBar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
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
        List<String> ls = new CopyOnWriteArrayList<>();

        ls.add("a");
        ls.add("b");
        ls.add("c");
//
//        for (int i = 0; i < ls.size(); i++) {
//            System.out.println(ls.get(i));
//            ls.add(1,"f" + i);
//        }
        int i = 0;

        for (String l : ls) {
            System.out.println(l);
            ls.add("f" + i++);
        }

        System.out.println(ls.size());

    }


//    public static void main(String[] args) {
//        ProgressBar progressBar = new ProgressBar();
//
//
//        Executor executor = Executors.newCachedThreadPool();
//
//        Runnable run = new Runnable() {
//            @Override
//            public void run() {
//                AtomicLong num = new AtomicLong(40);
//                progressBar.add(num);
//                for (;;) {
//                    num.decrementAndGet();
//                    try {
//                        Thread.sleep(300);
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//            }
//        };
//
//        executor.execute(run);
//        executor.execute(run);
//
//        progressBar.show();
//    }



}
