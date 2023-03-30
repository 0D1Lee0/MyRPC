package com.rpc.transport.socket.server;

import com.rpc.factory.ThreadPoolFactory;
import com.rpc.handler.RequestHandler;
import com.rpc.hook.ShutdownHook;
import com.rpc.provider.ServiceProviderImpl;
import com.rpc.registry.NacosServiceRegistry;
import com.rpc.serializer.CommonSerializer;
import com.rpc.transport.AbstractRpcServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

/**
 * @description Socket方式进行远程调用连接的服务端
 */
public class SocketServer extends AbstractRpcServer {

    private final ExecutorService threadPool;
    private RequestHandler requestHandler = new RequestHandler();

    private final CommonSerializer serializer;

    public SocketServer(String host, int port) {
        this(host, port, DEFAULT_SERIALIER);
    }

    public SocketServer(String host, int port, Integer serializerCode) {
        this.host = host;
        this.port = port;
        // 创建线程池
        threadPool = ThreadPoolFactory.createDefaultThreadPool("socket-rpc-server");
        serviceRegistry = new NacosServiceRegistry();
        serviceProvider = new ServiceProviderImpl();
        serializer = CommonSerializer.getByCode(serializerCode);
        //自动注册服务
        scanServices();
    }

    @Override
    /**
     * @description 服务端启动
     */
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.bind(new InetSocketAddress(host, port));
            logger.info("服务器正在启动...");
            //添加钩子，服务端关闭时会注销服务
            ShutdownHook.getShutdownHook().addClearAllHook();
            Socket socket;
            while ((socket = serverSocket.accept()) != null) {
                logger.info("客户端连接！Ip为：" + socket.getInetAddress());
                threadPool.execute(new SocketRequestHandlerThread(socket, requestHandler, serializer));
            }
            threadPool.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
