package com.rpc.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * @description 一致性哈希算法实现负载均衡
 */
public class ConsistentHashLoadBalancer implements LoadBalancer{
    @Override
    public Instance select(List<Instance> instances) {
        return null;
    }
}
