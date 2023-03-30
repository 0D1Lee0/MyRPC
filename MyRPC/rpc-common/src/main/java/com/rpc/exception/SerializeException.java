package com.rpc.exception;

/**
 * @description 序列化异常
 */
public class SerializeException extends RuntimeException{
    public SerializeException(String msg){
        super(msg);
    }
}
