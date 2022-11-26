import org.junit.Test;

/**
 * //TODO add class commment here
 *
 * @Author wfh
 * @Date 2022/11/26 下午5:20
 */
public class CommandExecutorTest {
    @Test
    public void downloadtest() {
        String message = "download makabka to mydir";
        String resourceId = message.substring("download ".length(), message.lastIndexOf("to")).trim();
        String downDir = message.substring(message.lastIndexOf("to") + 2).trim();

        System.out.println(resourceId);
        System.out.println(downDir);
    }
}
