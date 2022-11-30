package org.xulinux.yuki.common;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

/**
 * //TODO add class commment here
 *
 * @Author wfh
 * @Date 2022/11/27 下午9:01
 */
public class ProgressBar {
    private List<Long> origin;
    private List<AtomicLong> cur;
    private Speaker speaker;

    public ProgressBar() {
        this.origin = new CopyOnWriteArrayList<>();
        this.cur = new CopyOnWriteArrayList<>();
    }

    public void add(AtomicLong num) {
        this.origin.add(num.get());
        this.cur.add(num);
    }

    public void show() {
        String prefix = "\r接收进度：";
        StringBuilder sb = new StringBuilder(prefix);

        for (; ; ) {
            boolean hasUnfinished = false;

            sb.delete(prefix.length(), sb.length());
            for (int i = 0; i < origin.size(); i++) {
                long ori = origin.get(i);
                long cu = cur.get(i).get();

                sb.append(ori - cu)
                        .append("/")
                        .append(ori)
                        .append(" ");
                if (cu > 0) {
                    hasUnfinished = true;
                }
            }
            speaker.speak(sb.toString());

            // todo 这里会有bug
            if (!hasUnfinished && origin.size() != 0)  {
                break;
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void setSpeaker(Speaker speaker) {
        this.speaker = speaker;
    }
}
