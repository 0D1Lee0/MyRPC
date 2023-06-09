package com.rpc.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.rpc.entity.RpcRequest;
import com.rpc.entity.RpcResponse;
import com.rpc.enumeration.SerializerCode;
import com.rpc.exception.SerializeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class KryoSerializer implements CommonSerializer {
    private static final Logger logger = LoggerFactory.getLogger(KryoSerializer.class);
    //使用ThreadLocal初始化Kryo，因为Kryo中的output和input是线程不安全的
    private static final ThreadLocal<Kryo> kryoThreadlocal = ThreadLocal.withInitial(()->{
        Kryo kryo=new Kryo();
        //注册类
        kryo.register(RpcResponse.class);
        kryo.register(RpcRequest.class);
        //循环引用检测，默认为true
        kryo.setReferences(true);
        //不强制要求注册类，默认为false，若设置为true则要求涉及到的所有类都要注册，包括jdk中的比如Object
        kryo.setRegistrationRequired(false);
        return kryo;
    });
    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             Output output=new Output(byteArrayOutputStream)){
            Kryo kryo=kryoThreadlocal.get();
            kryo.writeObject(output,obj);
            kryoThreadlocal.remove();
            return output.toBytes();
        }
        catch (Exception e){
            logger.error("序列化时有错误发生:", e);
            throw new SerializeException("序列化时有错误发生");
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try(ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            Input input = new Input(byteArrayInputStream)){
            Kryo kryo=kryoThreadlocal.get();
            Object obj=kryo.readObject(input,clazz);
            kryoThreadlocal.remove();
            return obj;

        }
        catch (Exception e){
            logger.error("反序列化时有错误发生:", e);
            throw new SerializeException("反序列化时有错误发生");
        }
    }

    @Override
    public int getCode() {
        return SerializerCode.valueOf("KRYO").getCode();
    }
}
