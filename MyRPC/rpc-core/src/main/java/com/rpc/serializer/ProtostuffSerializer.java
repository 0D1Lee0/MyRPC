package com.rpc.serializer;

import com.rpc.enumeration.SerializerCode;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description Protostuff序列化器
 */
public class ProtostuffSerializer implements CommonSerializer {
    private static final Logger logger = LoggerFactory.getLogger(ProtostuffSerializer.class);

    /**
     * 避免每次序列化都重新申请Buffer空间,用来存放对象序列化之后的数据
     * 如果你设置的空间不足，会自动扩展的，但这个大小还是要设置一个合适的值，设置大了浪费空间，设置小了会自动扩展浪费时间
     */
    private LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);

    /**
     * 缓存类对应的Schema，由于构造schema需要获得对象的类和字段信息，会用到反射机制
     *这是一个很耗时的过程，因此进行缓存很有必要，下次遇到相同的类直接从缓存中get就行了
     */
    private Map<Class<?>, Schema<?>> schemaCache = new ConcurrentHashMap<>();
    @Override
    public byte[] serialize(Object obj) {
        Class clazz=obj.getClass();
        Schema schema = getSchema(clazz);
        byte[] data;
        try{
            // 序列化操作，将对象转换为字节数组
            data = ProtostuffIOUtil.toByteArray(obj,schema,buffer);
        }
        finally {
            // 使用完清空buffer
            buffer.clear();
        }
        return data;
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        Schema schema=getSchema(clazz);
        Object obj=schema.newMessage();
        // 反序列化操作，将字节数组转换为对应的对象
        ProtostuffIOUtil.mergeFrom(bytes,obj,schema);
        return obj;
    }

    @Override
    public int getCode() {
        return SerializerCode.valueOf("PROTOSTUFF").getCode();
    }

    /**
     * @description 获取Schema
     * @param clazz
     * @return [io.protostuff.Schema]
     */
    private Schema getSchema(Class clazz) {

        Schema schema = schemaCache.get(clazz);
        if(Objects.isNull(schema)) {
            schema= RuntimeSchema.getSchema(clazz);
            if(Objects.nonNull(schema)){
                schemaCache.put(clazz,schema);
            }
        }
        return schema;
    }
}