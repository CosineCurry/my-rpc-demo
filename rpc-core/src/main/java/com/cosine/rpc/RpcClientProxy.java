package com.cosine.rpc;


import com.cosine.rpc.entity.RpcRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @Description: RPC客户端动态代理
 * @Author: cosine
 * @Date: 2021/10/2 8:01 下午
 * @Version: 1.0.0
 */
public class RpcClientProxy implements InvocationHandler {

    private static final Logger logger = LoggerFactory.getLogger(RpcClientProxy.class);

    private final RpcClient client;

    public RpcClientProxy(RpcClient client) {
        this.client = client;
    }

    /**
     * @Description 生成代理对象
     * @Param [clazz]
     * @return T
     * @Author cosine
     * @Date 2021/10/3
     */
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        // 三个参数分别为委托类的类加载器，委托类实现的接口集，处理类本身
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    /**
     * @Description 代理对象的方法被调用时的动作
     * @Param [proxy, method, args]
     * @return java.lang.Object
     * @Author cosine
     * @Date 2021/10/3
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        logger.info("调用方法: {}#{}", method.getDeclaringClass().getName(), method.getName());
        RpcRequest rpcRequest = new RpcRequest(method.getDeclaringClass().getName(),
                method.getName(), args, method.getParameterTypes());
        return client.sendRequest(rpcRequest);
    }
}
