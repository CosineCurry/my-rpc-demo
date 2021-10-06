package com.cosine.test;

import com.cosine.rpc.RpcClient;
import com.cosine.rpc.RpcClientProxy;
import com.cosine.rpc.api.HelloObject;
import com.cosine.rpc.api.HelloService;
import com.cosine.rpc.netty.client.NettyClient;

/**
 * @Description: 测试用Netty消费者（客户端）
 * @Author: cosine
 * @Date: 2021/10/6 3:04 下午
 * @Version: 1.0.0
 */
public class NettyTestClient {
    public static void main(String[] args) {
        RpcClient client = new NettyClient("127.0.0.1", 9002);
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(16, "Netty NiO形式的数据");
        String res = helloService.hello(object);
        System.out.println(res);
    }
}
