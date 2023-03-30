package test.server_1;
import com.rpc.annotation.ServiceScan;
import com.rpc.serializer.CommonSerializer;
import com.rpc.transport.RpcServer;
import com.rpc.transport.netty.server.NettyServer;


@ServiceScan
public class NServer1 {
    public static void main(String[] args){
        RpcServer server = new NettyServer("127.0.0.1",9991, CommonSerializer.PROTOBUF_SERIALIZER);
        server.start();
    }
}
