package org.xulinux.yuki.registry;

import org.xulinux.yuki.common.NodeInfo;

import java.util.List;
import java.util.stream.Collectors;

/**
 * //TODO add class commment here
 *
 * @Author wfh
 * @Date 2022/10/8 下午8:19
 */
public class DefaultLoadBalance implements LoadBalance{

    @Override
    public List<NodeInfo> select(List<NodeInfo> nodes, int maxReceive) {
        return nodes
                .stream()
                .filter( nodeInfo -> nodeInfo.getNowServicing() < nodeInfo.getMaxServicing())
                .sorted((n1, n2) -> {
                    float factor1 = ((float) n1.getNowServicing()) / n1.getMaxServicing();
                    float factor2 = ((float) n2.getNowServicing()) / n2.getMaxServicing();

                    float result = factor1 - factor2;

                    if (result > 0) {
                        return 1;
                    } else if (result < 0) {
                        return -1;
                    }

                    return 0;
                })
                .limit(maxReceive)
                .collect(Collectors.toList());
    }
}
