package com.rpc.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;
import java.util.List;

/**
 * @description 轮询选择一个
 */
public class RoundRobinLoadBalancer implements LoadBalancer {

    /**
     * index表示当前选到了第几个服务器，并且每次选择后都会自增一
     */
    private int index=0;

    @Override
    public Instance select(List<Instance> instances) {
        index = (index+1)%instances.size();
        return instances.get(index);
    }
}
