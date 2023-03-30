package test;


import com.rpc.api.ByeService;
import com.rpc.api.HelloObject;
import com.rpc.api.HelloService;
import com.rpc.loadbalancer.RoundRobinLoadBalancer;
import com.rpc.serializer.CommonSerializer;
import com.rpc.transport.RpcClient;
import com.rpc.transport.RpcClientProxy;
import com.rpc.transport.netty.client.NettyClient;


public class NettyTestClient {
    public static void main(String[] args){
        RpcClient client=new NettyClient(CommonSerializer.PROTOBUF_SERIALIZER,new RoundRobinLoadBalancer());
        RpcClientProxy rpcClientProxy=new RpcClientProxy(client);
        HelloService helloService = (HelloService) rpcClientProxy.getProxy(HelloService.class);
        /**
        HelloObject object = new HelloObject(12, "this is netty style");
        String res = helloService.hello(object);
        System.out.println(res);
        ByeService byeService = (ByeService) rpcClientProxy.getProxy(ByeService.class);
        System.out.println(byeService.bye("Netty"));
         **/
        for(int i=0;i<10;i++){
            HelloObject object = new HelloObject(12+i, "this is netty style");
            String res = helloService.hello(object);
            System.out.println(res);
            //ByeService byeService = (ByeService) rpcClientProxy.getProxy(ByeService.class);
            //System.out.println(byeService.bye("Netty:Bye!"+12+i));
        }
        //System.exit(0);
    }
}
