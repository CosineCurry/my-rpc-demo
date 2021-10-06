package com.cosine.test;

import com.cosine.rpc.api.HelloService;
import com.cosine.rpc.netty.server.NettyServer;
import com.cosine.rpc.registry.DefaultServiceRegistry;
import com.cosine.rpc.registry.ServiceRegistry;


/**
 * @Description: 测试用Netty服务提供方（服务端）
 * @Author: cosine
 * @Date: 2021/10/6 3:05 下午
 * @Version: 1.0.0
 */
public class NettyTestServer {

    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        ServiceRegistry registry = new DefaultServiceRegistry();
        // 服务注册
        registry.register(helloService);
        NettyServer server = new NettyServer();
        server.start(9002);
    }

}
