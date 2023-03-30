package com.rpc.transport.netty.server;

import com.rpc.entity.RpcRequest;
import com.rpc.factory.ThreadPoolFactory;
import com.rpc.handler.RequestHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

/**
 * com.server.NettyServerHandler 位于服务器端责任链尾部，直接和 RpcServer 对象
 * NettyServerhandler 用于接收 com.RpcRequest，并且执行调用，将调用结果返回封装成 RpcResponse 发送出去。
 * @description Netty中处理从客户端传来的RpcRequest
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);

    private static final String THREAD_NAME_PREFIX = "netty-server-handler";
    private RequestHandler requestHandler;
    private final ExecutorService threadPool;
    public NettyServerHandler() {
        requestHandler = new RequestHandler();
        // 引入异步业务线程池，避免长时间的耗时业务阻塞netty本身的worker工作线程，耽误了同一个Selector中其他任务的执行
        threadPool= ThreadPoolFactory.createDefaultThreadPool(THREAD_NAME_PREFIX);
    }
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            IdleState state = ((IdleStateEvent) evt).state();
            if(state == IdleState.READER_IDLE){
                logger.info("长时间未收到心跳包，断开连接……");
                ctx.close();
            }
            else{
                super.userEventTriggered(ctx, evt);
            }
        }
    }
    //
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
        try{
            if(msg.getHeartBeat()){
                logger.info("接收到客户端心跳包……");
                return;
            }
            logger.info("服务器接收到请求: {}", msg);
            Object response = requestHandler.handle(msg);
            if(ctx.channel().isActive() && ctx.channel().isWritable()){
                //注意这里的通道是workGroup中的，而NettyServer中创建的是bossGroup的，不要混淆
                ctx.writeAndFlush(response);
            }
            else{
                logger.error("通道不可写");
            }
        }finally {
            ReferenceCountUtil.release(msg);
            /**
             logger.info("关闭服务端");
             // 强制关闭服务端
             ctx.channel().parent().close();
             **/
        }

    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("处理过程调用时有错误发生:");
        cause.printStackTrace();
        ctx.close();
    }
}
