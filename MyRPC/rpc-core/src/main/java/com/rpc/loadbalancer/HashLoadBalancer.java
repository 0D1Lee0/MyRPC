package com.rpc.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * @description 哈希算法实现负载均衡
 */
public class HashLoadBalancer implements LoadBalancer{
    @Override
    public Instance select(List<Instance> instances) {
        return null;
    }
}
