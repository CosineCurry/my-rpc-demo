package com.cosine.test;

import com.cosine.rpc.api.HelloService;
import com.cosine.rpc.registry.DefaultServiceRegistry;
import com.cosine.rpc.registry.ServiceRegistry;
import com.cosine.rpc.socket.server.SocketServer;

/**
 * @Description: 测试用Socket服务提供方（服务端）
 * @Author: cosine
 * @Date: 2021/10/5 9:12 下午
 * @Version: 1.0.0
 */
public class SocketTestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        // 解耦，可以替换掉ServiceRegistry的具体实现类从而不影响方法本身
        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        // 进行服务注册
        serviceRegistry.register(helloService);
        SocketServer socketServer = new SocketServer(serviceRegistry);
        // 服务启动
        socketServer.start(9001);
    }
}
