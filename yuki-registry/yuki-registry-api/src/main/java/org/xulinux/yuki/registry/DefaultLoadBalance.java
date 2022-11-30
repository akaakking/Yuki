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
