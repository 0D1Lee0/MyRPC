package com.rpc.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.rpc.enumeration.RpcError;
import com.rpc.exception.RpcException;
import com.rpc.util.NacosUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class NacosServiceRegistry implements ServiceRegistry{
    private static final Logger logger = LoggerFactory.getLogger(NacosServiceRegistry.class);
    /**
     * @description 将服务的名称和地址注册进服务注册中心
     * @param serviceName, inetSocketAddress
     * @return [void]
     */
    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            // 向nacos注册服务
            NacosUtil.registerService(serviceName,inetSocketAddress);
        }
        catch (NacosException e){
            logger.info("注册服务时有错误发生:", e);
            throw new RpcException(RpcError.REGISTER_SERVICE_FAILED);
        }

    }
}
