package com.cosine.rpc.netty.client;

import com.cosine.rpc.entity.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description: Netty客户端侧处理器，位于Netty责任链尾部
 * @Author: cosine
 * @Date: 2021/10/6 10:55 上午
 * @Version: 1.0.0
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private static final Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        try {
            logger.info(String.format("客户端接收到消息：%s", rpcResponse));
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
            channelHandlerContext.channel().attr(key).set(rpcResponse);
            channelHandlerContext.channel().close();
        } finally {
            ReferenceCountUtil.release(rpcResponse);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("过程调用时有错误发生:");
        cause.printStackTrace();
        ctx.close();
    }
}
