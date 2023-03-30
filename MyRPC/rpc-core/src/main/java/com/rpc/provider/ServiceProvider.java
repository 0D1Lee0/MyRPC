package com.rpc.provider;

/**
 * @description 保存提供服务实例对象
 */
public interface ServiceProvider {
    /**
     * @description 将一个服务注册进注册表
     * @param service
     * @param serviceName
     * @param <T>
     */
    <T> void addServiceProvider(T service, String serviceName);
    /**
     * @description 根据服务名获取服务实体
     * @param serviceName 服务名称
     * @return [java.lang.Object] 服务实体
     *
     **/
    Object getServiceProvider(String serviceName);
}
