package test;

import com.rpc.annotation.ServiceScan;
import com.rpc.serializer.CommonSerializer;
import com.rpc.transport.RpcServer;
import com.rpc.transport.netty.server.NettyServer;

@ServiceScan
public class NettyTestServer {
    public static void main(String[] args){
        RpcServer server = new NettyServer("127.0.0.1",9999, CommonSerializer.PROTOBUF_SERIALIZER);
        server.start();
    }
}
