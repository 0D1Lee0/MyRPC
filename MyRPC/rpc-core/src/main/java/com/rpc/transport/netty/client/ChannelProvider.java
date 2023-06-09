package com.rpc.transport.netty.client;

import com.rpc.codec.CommonDecoder;
import com.rpc.codec.CommonEncoder;
import com.rpc.serializer.CommonSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class ChannelProvider {
    private static final Logger logger = LoggerFactory.getLogger(ChannelProvider.class);
    private static EventLoopGroup eventLoopGroup;
    private static Bootstrap bootstrap = initializeBootstrap();
    /**
     * 所有客户端Channel都保存在该Map中
     */
    private static Map<String, Channel> channels = new ConcurrentHashMap<>();

    public static Channel get(InetSocketAddress inetSocketAddress, CommonSerializer serializer) {
        String key = inetSocketAddress.toString()+serializer.getCode();
        if(channels.containsKey(key)){
            Channel channel = channels.get(key);
            if(channel!=null && channel.isActive()){
                return channel;
            }
            else{
                channels.remove(key);
            }
        }
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                /*自定义序列化编解码器*/
                // RpcResponse -> ByteBuf
                ch.pipeline().addLast(new CommonEncoder(serializer))
                        // 设定IdleStateHandler心跳检测每5秒进行一次写检测，如果5秒内write()方法未被调用则触发一次userEventTrigger()方法
                        // 实现客户端每5秒向服务端发送一次消息
                        .addLast(new IdleStateHandler(0,5,0, TimeUnit.SECONDS))
                        .addLast(new CommonDecoder())
                        .addLast(new NettyClientHandler());
            }
        });
        Channel channel = null;
        try{
            channel = connect(bootstrap,inetSocketAddress);
        }
        catch (ExecutionException | InterruptedException e){
            logger.error("连接客户端时有错误发生", e);
            return null;
        }
        channels.put(key,channel);
        return channel;
    }

    /**
     * @description Netty客户端创建通道连接
     * @param bootstrap
     * @param inetSocketAddress
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private static Channel connect(Bootstrap bootstrap, InetSocketAddress inetSocketAddress) throws ExecutionException, InterruptedException {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            if(future.isSuccess()){
                logger.info("客户端连接成功！");
                completableFuture.complete(future.channel());
            }
            else{
                throw new IllegalStateException();
            }
        });
        return completableFuture.get();
    }
    private static Bootstrap initializeBootstrap() {
        eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                //连接的超时时间，超过这个时间还是建立不上的话则代表连接失败
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                //是否开启 TCP 底层心跳机制
                .option(ChannelOption.SO_KEEPALIVE, true)
                //TCP默认开启了 Nagle 算法，该算法的作用是尽可能的发送大数据快，减少网络传输。TCP_NODELAY 参数的作用就是控制是否启用 Nagle 算法。
                .option(ChannelOption.TCP_NODELAY, true);
        return bootstrap;
    }
}
