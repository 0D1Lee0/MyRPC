package com.rpc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 方法调用的响应状态码
 *
 */
@AllArgsConstructor
@Getter
public enum ResponseCode {
    /**
     * success:调用方法成功
     * fail:调用方法失败
     * not_found_method:未找到指定方法
     * not_found_class: 未找到指定类
     */
    SUCCESS(200, "调用方法成功"),
    FAIL(500, "调用方法失败"),
    NOT_FOUND_METHOD(501,"未找到指定方法"),
    NOT_FOUND_CLASS(502,"未找到指定类");
    /**
     * 响应状态码
     */
    private final int code;
    /**
     * 响应状态信息
     */
    private final String message;

}

