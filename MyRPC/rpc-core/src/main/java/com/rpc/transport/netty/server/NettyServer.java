package com.rpc.transport.netty.server;

import com.rpc.codec.CommonDecoder;
import com.rpc.codec.CommonEncoder;
import com.rpc.hook.ShutdownHook;
import com.rpc.provider.ServiceProviderImpl;
import com.rpc.registry.NacosServiceRegistry;
import com.rpc.serializer.CommonSerializer;
import com.rpc.transport.AbstractRpcServer;
import com.rpc.transport.netty.server.NettyServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class NettyServer extends AbstractRpcServer {

    private final CommonSerializer serializer;

    public NettyServer(String host, int port) {
        this(host, port, DEFAULT_SERIALIER);
    }

    public NettyServer(String host, int port, Integer serializerCode) {
        this.host = host;
        this.port = port;
        serviceRegistry = new NacosServiceRegistry();
        serviceProvider = new ServiceProviderImpl();
        serializer = CommonSerializer.getByCode(serializerCode);
        scanServices();
    }

    @Override
    public void start() {
        //添加注销服务的钩子，服务端关闭时才会执行
        ShutdownHook.getShutdownHook().addClearAllHook();
        // 用于处理客户端新连接的主”线程池“，负责分发请求
        EventLoopGroup bossGroup = new NioEventLoopGroup();     //用于处理服务器端接收客户端连接
        // 用于连接后处理IO事件的从”线程池“，负责执行相应的Handler
        EventLoopGroup workerGroup = new NioEventLoopGroup();   //进行网络通信（读写）
        try {
            // 辅助工具类，用于服务器通道的一系列配置
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            // 绑定两个线程组,将主从“线程池”初始化到启动器中
            serverBootstrap.group(bossGroup, workerGroup)
                    // 设置服务端通道类型,指定NIO模式
                    .channel(NioServerSocketChannel.class)
                    // 日志打印方式
                    .handler(new LoggingHandler(LogLevel.INFO))
                    //设置TCP缓冲区，配置ServerChannel参数，服务端接受连接的最大队列长度，如果队列已满，客户端连接将被拒绝
                    .option(ChannelOption.SO_BACKLOG, 256)
                    /**
                     * 启用该功能时，TCP会主动探测空闲连接的有效性。可以将此功能视为TCP的心跳机制，默认的心跳间隔是7200s即2小时
                     * 这里需要用childOption(ChannelOption.SO_KEEPALIVE,true)，使用Option(ChannelOption.SO_KEEPALIVE,true) 会报错
                     * Unknown channel option 'SO_KEEPALIVE' for channel '
                     */
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    //配置Channel参数，nodelay没有延迟，true就代表禁用Nagle算法，减小传输延迟
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    //配置具体的数据处理方式，初始化Handler,设置Handler操作
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        //这里会进行客户端业务处理
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            //往管道中添加Handler，注意入站Handler与出站Handler都必须按实际执行顺序添加，比如先解码再Server处理，那Decoder()就要放在前面
                            //设定IdleStateHandler心跳检测每30秒进行一次读检测，如果30秒内ChannelRead()方法未被调用则触发一次userEventTrigger()方法
                            pipeline.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
                            pipeline.addLast(new CommonEncoder(serializer));
                            pipeline.addLast(new CommonDecoder());
                            pipeline.addLast(new NettyServerHandler());
                        }
                    });
            //绑定端口，启动Netty，sync()代表阻塞主Server线程，以执行Netty线程，如果不阻塞Netty就直接被下面shutdown了
            ChannelFuture future = serverBootstrap.bind(host, port).sync();

            //serverChannel=future.channel();
            //等确定通道关闭了，关闭future回到主Server线程
            future.channel().closeFuture().sync();
            //serverChannel.closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("启动服务器时有错误发生: ", e);
        } finally {
            //优雅关闭Netty服务端且清理掉内存
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
