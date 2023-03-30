package test.server_2;
import com.rpc.annotation.ServiceScan;
import com.rpc.serializer.CommonSerializer;
import com.rpc.transport.RpcServer;
import com.rpc.transport.netty.server.NettyServer;


@ServiceScan
public class NServer2 {
    public static void main(String[] args){
        RpcServer server = new NettyServer("127.0.0.1",9992, CommonSerializer.PROTOBUF_SERIALIZER);
        server.start();
    }
}
