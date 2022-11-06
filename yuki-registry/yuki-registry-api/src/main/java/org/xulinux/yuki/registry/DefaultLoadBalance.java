package org.xulinux.yuki.registry;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * //TODO add class commment here
 *
 * @Author wfh
 * @Date 2022/10/8 下午8:19
 */
public class DefaultLoadBalance implements LoadBalance{

    // 根据负载因子 能够承受的最大的和现在正在承受的，这种就可以看作是一种柔性负载。但是很难受的一点是不支持缓存了，。
    // 要不搞一个一致性hash？
    @Override
    public List<NodeInfo> select(List<NodeInfo> nodes, int maxReceive) {
        return nodes
                .stream()
                .filter( nodeInfo -> nodeInfo.getNowServicing() < nodeInfo.getMaxServicing())
                .sorted((n1,n2) -> {
                        float num =  (float) n1.getMaxServicing() / n1.getNowServicing() -  n2.getMaxServicing() / n2.getNowServicing();
                        if (num == 0) {
                            return 0;
                        } else if (num > 0) {
                            return -1;
                        } else {
                            return 1;
                        }
                        })
                .limit(maxReceive)
                .collect(Collectors.toList());
    }
}
