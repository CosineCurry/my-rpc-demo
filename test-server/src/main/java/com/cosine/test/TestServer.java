package com.cosine.test;

import com.cosine.rpc.api.HelloService;
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
        RpcServer rpcServer = new RpcServer();
        rpcServer.register(helloService, 9001);
    }
}
