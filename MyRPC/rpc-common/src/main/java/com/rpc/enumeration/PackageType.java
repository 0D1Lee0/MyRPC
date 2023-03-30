package com.rpc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PackageType {
    /**
     *  0 表示RpcRequest
     *  1 表示RpcResponse
     */
    REQUEST_PACK(0),
    RESPONSE_PACK(1);

    private final int code;
}
