package com.rpc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Data 生成getter,setter ,toString等函数
 * @AllArgsConstructor 生成全参数构造函数
 * @NoArgsConstructor 生成无参构造函数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcRequest implements Serializable {
    /**
     * 序列化需要无参构造函数
     * 具体规则
     * 1.单一对象,无继承关系:若想实现序列化与反序列化,则必须实现序列化接口,否则报异常:NotSerializableException
     * 2.对象间有继承关系,但无引用关系,若想实现序列化与反序列化,则父类必须实现序列化接口或提供无参构造函数,否则报invalidClassException
     * 3.对象间有继承关系,并且有引用关系,若想实现序列化与反序列化,则父类必须实现序列化接口
     */
    //public com.RpcRequest() {}
    /**
     * 请求号
     */
    private String requestId;
    /**
     * 待调用接口名称
     */
    private String interfaceName;
    /**
     * 待调用方法名称
     */
    private String methodName;
    /**
     * 调用方法的参数
     */
    private Object[] parameters;
    /**
     * 调用方法的参数类型
     */
    private Class<?>[] paramTypes;
    /**
     * 是否是心跳包
     * 心跳包是由客户端发送到服务端的，告诉服务端在线
     */
    private Boolean heartBeat;
}
