package com.rpc.transport.socket.util;

import com.rpc.entity.RpcRequest;
import com.rpc.entity.RpcResponse;
import com.rpc.enumeration.PackageType;
import com.rpc.serializer.CommonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @description Socket方式将数据序列化并写入输出流中【编码】
 */
public class ObjectWriter {
    private static final Logger logger = LoggerFactory.getLogger(ObjectWriter.class);
    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    public static void writeObject(OutputStream out, Object object, CommonSerializer serializer) throws IOException {
        //魔数，判断文件类型
        out.write(intToBytes(MAGIC_NUMBER));
        //消息类型
        if(object instanceof RpcRequest){
            out.write(intToBytes(PackageType.REQUEST_PACK.getCode()));
        }
        else if(object instanceof RpcResponse){
            out.write(intToBytes(PackageType.RESPONSE_PACK.getCode()));
        }
        //序列化器
        out.write(intToBytes(serializer.getCode()));
        byte[] bytes=serializer.serialize(object);
        //数据长度
        out.write(intToBytes(bytes.length));
        //具体数据
        out.write(bytes);
        out.flush();
    }

    /**
     * @description 将Int转换为字节数组
     * @param value
     * @return [byte[]]
     */
    private static byte[] intToBytes(int value) {
        byte[] src = new byte[4];
        src[0] = (byte) ((value>>24) & 0xFF);
        src[1] = (byte) ((value>>16) & 0xFF);
        src[2] = (byte) ((value>>8) & 0xFF);
        src[3] = (byte) (value & 0xFF);
        return src;
    }
}
