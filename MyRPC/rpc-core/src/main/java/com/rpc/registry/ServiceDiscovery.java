package com.rpc.registry;

import java.net.InetSocketAddress;

/**
 * @description 服务发现接口
 */
public interface ServiceDiscovery {
    /**
     * @descrip 服务发现接口
     * @param serviceName 服务名
     * @return InetSocketAddress 服务端地址
     */
    InetSocketAddress lookupService(String serviceName);
}