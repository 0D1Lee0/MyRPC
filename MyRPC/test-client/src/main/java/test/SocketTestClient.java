package test;

import com.rpc.annotation.ServiceScan;
import com.rpc.api.ByeService;
import com.rpc.api.HelloObject;
import com.rpc.api.HelloService;
import com.rpc.loadbalancer.RandomLoadBalancer;
import com.rpc.serializer.CommonSerializer;
import com.rpc.transport.RpcClient;
import com.rpc.transport.RpcClientProxy;
import com.rpc.transport.socket.client.SocketClient;

@ServiceScan
public class SocketTestClient {
    public static void main(String[] args){
        RpcClient client=new SocketClient(CommonSerializer.KRYO_SERIALIZER,new RandomLoadBalancer());
        //接口与代理对象之间的中介对象
        RpcClientProxy proxy = new RpcClientProxy(client);
        //创建代理对象
        HelloService helloService = (HelloService) proxy.getProxy(HelloService.class);
        ByeService byeService = (ByeService) proxy.getProxy(ByeService.class);
        //接口方法的参数对象
        HelloObject object = new HelloObject(12, "Hello,world!");
        for(int i=0;i<10;i++){
            //由动态代理可知，代理对象调用hello()实际会执行invoke()
            String res1 = helloService.hello(object);
            System.out.println(res1);
            String res2 = byeService.bye("Bye bye");
            System.out.println(res2);
        }
    }

}
