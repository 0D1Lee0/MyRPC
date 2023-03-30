package com.rpc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SerializerCode {
    /**
     * kryo: kryo序列化
     * json: json序列化
     * hessian: hession序列化
     * protostuff: prostuff序列化
     */
    KRYO(0),
    JSON(1),
    HESSIAN(2),
    PROTOSTUFF(3);

    private final int code;

}
