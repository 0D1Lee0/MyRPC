package com.rpc.handler;

import com.rpc.entity.RpcRequest;
import com.rpc.entity.RpcResponse;
import com.rpc.enumeration.ResponseCode;
import com.rpc.provider.ServiceProvider;
import com.rpc.provider.ServiceProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
/**
 * @description 实际执行方法调用的处理器
 */
public class RequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private static final ServiceProvider serviceProvider;
    static{
        serviceProvider = new ServiceProviderImpl();
    }
    public Object handle(RpcRequest rpcRequest) {
        // 从服务端本地注册表中获取服务实体;
        Object service = serviceProvider.getServiceProvider(rpcRequest.getInterfaceName());
        return invokeTargetMethod(rpcRequest,service);
    }
    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service) {
        Object result;
        try{
            // 漏了 rpcRequest.getParamTypes()，会报错：RpcResponse cannot be cast to java.lang.String
            //getClass()获取的是实例对象的类型
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            result = method.invoke(service, rpcRequest.getParameters());
            logger.info("服务：{} 成功调用方法：{}", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        }
        catch(NoSuchMethodException | IllegalAccessException | InvocationTargetException e){
            // 方法调用失败
            return RpcResponse.fail(ResponseCode.NOT_FOUND_METHOD, rpcRequest.getRequestId());
        }
        //方法调用成功
        return RpcResponse.success(result, rpcRequest.getRequestId());
    }

}