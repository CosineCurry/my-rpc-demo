package com.cosine.rpc.netty.server;

import com.cosine.rpc.RequestHandler;
import com.cosine.rpc.entity.RpcRequest;
import com.cosine.rpc.entity.RpcResponse;
import com.cosine.rpc.registry.DefaultServiceRegistry;
import com.cosine.rpc.registry.ServiceRegistry;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description: Netty中处理RpcRequest的Handler，责任链尾端
 * @Author: cosine
 * @Date: 2021/10/6 2:52 下午
 * @Version: 1.0.0
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);

    /** 请求处理器 */
    private static RequestHandler requestHandler;
    /** 服务注册表 */
    private static ServiceRegistry serviceRegistry;

    static {
        requestHandler = new RequestHandler();
        serviceRegistry = new DefaultServiceRegistry();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {
        try {
            logger.info("服务器接收到请求：{}", rpcRequest);
            String interfaceName = rpcRequest.getInterfaceName();
            Object service = serviceRegistry.getService(interfaceName);
            Object result = requestHandler.handle(rpcRequest, service);
            // 一个 ChannelFuture 代表了一个还没有发生的 I/O 操作。
            // 这意味着任何一个请求操作都不会马上被执行，因为在 Netty 里所有的操作都是异步的。
            ChannelFuture future = channelHandlerContext.writeAndFlush(RpcResponse.success(result));
            // 由监听器通知我们 I/O 操作完成
            future.addListener(ChannelFutureListener.CLOSE);
        } finally {
            ReferenceCountUtil.release(rpcRequest);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("处理过程调用时有错误发生:");
        cause.printStackTrace();
        ctx.close();
    }
}
