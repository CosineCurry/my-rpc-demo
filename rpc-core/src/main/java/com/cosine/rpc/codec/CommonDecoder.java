package com.cosine.rpc.codec;

import com.cosine.rpc.entity.RpcRequest;
import com.cosine.rpc.entity.RpcResponse;
import com.cosine.rpc.enumeration.PackageType;
import com.cosine.rpc.enumeration.RpcError;
import com.cosine.rpc.exception.RpcException;
import com.cosine.rpc.serializer.CommonSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @Description: 通用解码拦截器
 * @Author: cosine
 * @Date: 2021/10/5 10:05 下午
 * @Version: 1.0.0
 */
public class CommonDecoder extends ReplayingDecoder {

    private static final Logger logger = LoggerFactory.getLogger(CommonDecoder.class);
    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        /** 魔数 */
        int magic = byteBuf.readInt();
        if (magic != MAGIC_NUMBER) {
            logger.error("不识别的协议包：{}", magic);
            throw new RpcException(RpcError.UNKNOWN_PROTOCOL);
        }
        /** 数据包编码 */
        int packageCode = byteBuf.readInt();
        Class<?> packageClass;
        if (packageCode == PackageType.REQUEST_PACK.getCode()) {
            packageClass = RpcRequest.class;
        } else if (packageCode == PackageType.RESPONSE_PACK.getCode()) {
            packageClass = RpcResponse.class;
        } else {
            logger.error("不识别的数据包：{}", packageCode);
            throw new RpcException(RpcError.UNKNOWN_PACKAGE_TYPE);
        }
        /** 序列化编码 */
        int serializerCode = byteBuf.readInt();
        CommonSerializer serializer = CommonSerializer.getByCode(serializerCode);
        if (serializer == null) {
            logger.error("不识别的反序列化器: {}", serializerCode);
            throw new RpcException(RpcError.UNKNOWN_SERIALIZER);
        }
        /** 数据长度 */
        int length = byteBuf.readInt();
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);
        // 进行解码
        Object obj = serializer.deserialize(bytes, packageClass);
        list.add(obj);
    }
}
