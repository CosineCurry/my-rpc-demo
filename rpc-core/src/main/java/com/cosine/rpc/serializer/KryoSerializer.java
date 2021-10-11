package com.cosine.rpc.serializer;

import com.cosine.rpc.entity.RpcRequest;
import com.cosine.rpc.entity.RpcResponse;
import com.cosine.rpc.enumeration.SerializerCode;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @Description: 使用Kryo格式的序列化器。kryo是一个高性能的序列化/反序列化工具，由于其变长存储特性并使用了
 *               字节码生成机制，拥有较高的运行速度和较小的体积。
 * @Author: cosine
 * @Date: 2021/10/11 2:32 下午
 * @Version: 1.0.0
 */
public class KryoSerializer implements CommonSerializer {

    private static final Logger logger = LoggerFactory.getLogger(KryoSerializer.class);

    /** Kryo 可能存在线程安全问题，文档上是推荐放在 ThreadLocal 里，一个线程一个 Kryo */
    private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(RpcResponse.class);
        kryo.register(RpcRequest.class);
        // 支持循环引用
        kryo.setReferences(true);
        // 关闭注册行为
        kryo.setRegistrationRequired(false);
        return kryo;
    });

    @Override
    public byte[] serialize(Object obj) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);
        // 通过ThreadLocal保证kryo对象的线程安全
        Kryo kryo = kryoThreadLocal.get();
        kryo.writeObject(output, obj);
        kryoThreadLocal.remove();
        return output.toBytes();
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Input input = new Input(byteArrayInputStream);
        Kryo kryo = kryoThreadLocal.get();
        Object o = kryo.readObject(input, clazz);
        kryoThreadLocal.remove();
        return o;
    }

    @Override
    public int getCode() {
        return SerializerCode.valueOf("KRYO").getCode();
    }
}
