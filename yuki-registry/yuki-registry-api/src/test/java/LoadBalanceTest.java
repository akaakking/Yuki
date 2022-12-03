import org.junit.Assert;
import org.junit.Test;
import org.xulinux.yuki.common.NodeInfo;
import org.xulinux.yuki.common.spi.ExtensionLoader;
import org.xulinux.yuki.registry.LoadBalance;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * //TODO add class commment here
 *
 * @Author wfh
 * @Date 2022/12/3 下午6:28
 */
public class LoadBalanceTest {
    @Test
    public void DefaultLoadBalanceTest() {
        LoadBalance balance = ExtensionLoader.getExtension(LoadBalance.class);
        NodeInfo node1 = new NodeInfo();
        NodeInfo node2 = new NodeInfo();

        node1.setIp("111");
        node1.setPort(111);
        node1.setMaxServicing(3);
        node1.setNowServicing(1);


        node2.setIp("111");
        node2.setPort(111);
        node2.setMaxServicing(3);
        node2.setNowServicing(0);

        List<NodeInfo> nodeInfos = new ArrayList<>();
        nodeInfos.add(node1);
        nodeInfos.add(node2);

        List<NodeInfo> select = balance.select(nodeInfos, 3);

        Assert.assertEquals(select.size(),2);
        Assert.assertEquals(select.get(0).getNowServicing(),0);
        Assert.assertEquals(select.get(1).getNowServicing(),1);
    }
}
