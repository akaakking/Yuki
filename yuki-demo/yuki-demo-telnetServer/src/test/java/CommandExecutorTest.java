import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * //TODO add class commment here
 *
 * @Author wfh
 * @Date 2022/11/26 下午5:20
 */
public class CommandExecutorTest {
    @Test
    public void COW() {
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();

        buf.writeBytes("abcde".getBytes(StandardCharsets.UTF_8));
        String str = buf.toString(0,2,StandardCharsets.UTF_8);
        buf.skipBytes(str.length());
        System.out.println(str);
        System.out.println(buf.readableBytes());
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
