package com.rpc.codec;

import com.rpc.entity.RpcRequest;
import com.rpc.enumeration.PackageType;
import com.rpc.serializer.CommonSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * com.CommonEncoder 的工作很简单，就是把 com.RpcRequest 或者 RpcResponse 包装成协议包
 * 自定义协议：
 * Magic Number 4 Bytes
 * com.PackageType 4 Bytes 确实是RpcRequest还是RpcResponse序列化后的字节
 * Serializer Type 4 Bytes
 * Data Length 4 Bytes 设置的目的是为了防止粘包
 * Data Bytes ${Data Length}
 */
public class CommonEncoder extends MessageToByteEncoder {
    private static final int MAGIC_NUMBER=0xCAFEBABE;
    private final CommonSerializer serializer;
    public CommonEncoder(CommonSerializer serializer) {
        this.serializer=serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        out.writeInt(MAGIC_NUMBER);
        if(msg instanceof RpcRequest){
            out.writeInt(PackageType.REQUEST_PACK.getCode());
        }
        else{
            out.writeInt(PackageType.RESPONSE_PACK.getCode());
        }
        out.writeInt(serializer.getCode());
        byte[] bytes=serializer.serialize(msg);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }
}
