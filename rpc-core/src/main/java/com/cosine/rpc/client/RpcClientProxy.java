package com.cosine.rpc.client;

import entity.RpcRequest;
import entity.RpcResponse;

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

    private String host;
    private int port;

    public RpcClientProxy(String host, int port) {
        this.host = host;
        this.port = port;
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
        // 使用建造者模式构建对象
        RpcRequest rpcRequest = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameters(args)
                .paramTypes(method.getParameterTypes())
                .build();
        RpcClient rpcClient = new RpcClient();
        return ((RpcResponse) rpcClient.sendRequest(rpcRequest, host, port)).getData();
    }
}
