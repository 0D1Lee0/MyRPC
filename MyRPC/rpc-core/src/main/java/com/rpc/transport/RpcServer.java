package com.rpc.transport;

import com.rpc.serializer.CommonSerializer;

public interface RpcServer {
    int DEFAULT_SERIALIER = CommonSerializer.KRYO_SERIALIZER;
    void start();

    /**
     * @description 向Nacos注册服务
     * @param service, serviceClass 服务实体，服务类名
     * @return [void]
     * @date [2021-03-13 15:56]
     */
    <T> void publishService(T service, String serviceName);
}