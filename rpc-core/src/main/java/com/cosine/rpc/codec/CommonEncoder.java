package com.cosine.rpc.codec;

import com.cosine.rpc.entity.RpcRequest;
import com.cosine.rpc.enumeration.PackageType;
import com.cosine.rpc.serializer.CommonSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @Description: 通用的编码拦截器，
 *               自定义协议格式魔数（4字节），包类型（4字节），序列化器（4字节），数据长度（4字节），具体数据（数据长度个字节）
 * @Author: cosine
 * @Date: 2021/10/5 10:06 下午
 * @Version: 1.0.0
 */
public class CommonEncoder extends MessageToByteEncoder {

    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    private final CommonSerializer serializer;

    public CommonEncoder(CommonSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        // 写入魔数
        byteBuf.writeInt(MAGIC_NUMBER);
        // 写入包类型
        if (o instanceof RpcRequest) {
            byteBuf.writeInt(PackageType.REQUEST_PACK.getCode());
        } else {
            byteBuf.writeInt(PackageType.RESPONSE_PACK.getCode());
        }
        // 写入序列化器
        byteBuf.writeInt(serializer.getCode());

        byte[] bytes = serializer.serialize(o);
        // 写入数据长度
        byteBuf.writeInt(bytes.length);
        // 写入数据
        byteBuf.writeBytes(bytes);

    }
}
