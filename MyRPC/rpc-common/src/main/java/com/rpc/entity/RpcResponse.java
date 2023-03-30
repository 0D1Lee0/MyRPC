package com.rpc.entity;

import lombok.Data;
import com.rpc.enumeration.ResponseCode;

import java.io.Serializable;
@Data
public class RpcResponse<T> implements Serializable {
    public RpcResponse() {}
    /**
     * 请求号
     */
    private String requestId;
    /**
     * 响应状态码
     */
    private Integer statusCode;
    /**
     * 响应状态补充信息
     */
    private String message;
    /**
     * 响应数据
     */
    private T data;

    /**
     * @description 成功时服务端返回的对象
     * @param data
     * @param <T>
     * @return RpcResponse<T>
     */
    public static<T> RpcResponse<T> success(T data,String requestId){
        RpcResponse<T> response=new RpcResponse<>();
        response.setRequestId(requestId);
        response.setStatusCode(ResponseCode.SUCCESS.getCode());
        response.setData(data);
        return response;
    }

    /**
     * @description 失败时服务端返回的对象
     * @param code
     * @param <T>
     * @return RpcResponse<T>
     */
    public static<T> RpcResponse<T> fail(ResponseCode code,String requestId){
        RpcResponse<T> response=new RpcResponse<>();
        response.setRequestId(requestId);
        response.setStatusCode(code.getCode());
        response.setMessage(code.getMessage());
        return response;
    }
}
