package com.cosine.test;

import com.cosine.rpc.api.HelloService;
import com.cosine.rpc.registry.DefaultServiceRegistry;
import com.cosine.rpc.registry.ServiceRegistry;
import com.cosine.rpc.server.RpcServer;

/**
 * @Description: 测试用服务提供方，目前只能注册一个端口
 * @Author: cosine
 * @Date: 2021/9/30 9:02 下午
 * @Version: 1.0.0
 */
public class TestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        // 解耦，可以替换掉ServiceRegistry的具体实现类从而不影响方法本身
        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        // 进行服务注册
        serviceRegistry.register(helloService);
        RpcServer rpcServer = new RpcServer(serviceRegistry);
        // 服务启动
        rpcServer.start(9001);
    }
}
