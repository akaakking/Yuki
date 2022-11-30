import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * //TODO add class commment here
 *
 * @Author wfh
 * @Date 2022/11/26 下午5:20
 */
public class CommandExecutorTest {

    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();

        for (int i = 0; i < nums.length; i++)  {

        }

        return result;
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
