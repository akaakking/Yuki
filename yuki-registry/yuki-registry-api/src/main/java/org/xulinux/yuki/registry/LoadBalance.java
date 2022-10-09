package org.xulinux.yuki.registry;


import java.util.List;

/**
 * //TODO add interface commment here
 *
 * @Author wfh
 * @Date 2022/10/8 下午8:15
 */
public interface LoadBalance {
    List<NodeInfo> select(List<NodeInfo> nodes, int maxReceive);
}
