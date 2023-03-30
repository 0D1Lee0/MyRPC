package com.rpc.util;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rpc.enumeration.RpcError;
import com.rpc.exception.RpcException;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @description 管理Nacos连接等工具类
 */
public class NacosUtil {
    private static final Logger logger= LoggerFactory.getLogger(NacosUtil.class);

    private static final NamingService namingService;
    private static final Set<String> servicenames = new HashSet<>();
    private static InetSocketAddress address;
    private static final String SERVER_ADDR = "127.0.0.1:8848";

    static {
        namingService = getNacosNamingService();
    }
    /**
     * @description 连接到Nacos创建命名空间
     * @return [com.alibaba.nacos.api.naming.NamingService]
     */
    public static NamingService getNacosNamingService(){
        try{
            return NamingFactory.createNamingService(SERVER_ADDR);
        }
        catch (NacosException e){
            logger.error("连接到Nacos时有错误发生：", e);
            throw new RpcException(RpcError.FAILED_TO_CONNECT_TO_SERVICE_REGISTRY);
        }
    }

    /**
     * @description 注册服务到Nacos
     * @param serviceName
     * @param address
     * @throws NacosException
     */
    public static void registerService(String serviceName, InetSocketAddress address) throws NacosException {
        namingService.registerInstance(serviceName, address.getHostName(), address.getPort());
        NacosUtil.address = address;
        // 保存注册的服务名
        servicenames.add(serviceName);
    }

    /**
     * @description 获取所有提供该服务的服务端地址
     * @param serviceName
     * @return
     * @throws NacosException
     */
    public static List<Instance> getAllInstance(String serviceName) throws NacosException {
        return namingService.getAllInstances(serviceName);
    }

    /**
     * @description 注销服务
     */
    public static void clearRegistry(){
        if(!servicenames.isEmpty() && address!=null){
            logger.info("开始注销服务");
            String host = address.getHostName();
            int port = address.getPort();
            // 利用迭代器迭代注销
            Iterator<String> iterator = servicenames.iterator();
            while(iterator.hasNext()){
                String serviceName = iterator.next();
                try{
                    logger.info("已注销服务{}",serviceName);
                    namingService.deregisterInstance(serviceName,host,port);
                }
                catch (NacosException e){
                    logger.error("注销服务{}失败", serviceName, e);
                }
            }
        }
    }
}

