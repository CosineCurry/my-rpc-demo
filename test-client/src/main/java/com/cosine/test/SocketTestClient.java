package com.cosine.test;

import com.cosine.rpc.RpcClient;
import com.cosine.rpc.RpcClientProxy;
import com.cosine.rpc.api.HelloObject;
import com.cosine.rpc.api.HelloService;
import com.cosine.rpc.socket.client.SocketClient;

/**
 * @Description: 测试用Socket消费者（客户端）
 * @Author: cosine
 * @Date: 2021/10/5 9:10 下午
 * @Version: 1.0.0
 */
public class SocketTestClient {
    public static void main(String[] args) {
        RpcClient client = new SocketClient("127.0.0.1", 9001);
        RpcClientProxy proxy = new RpcClientProxy(client);
        // 代理共有接口类 HelloService类
        HelloService helloService = proxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(13, "This is a message");
        // 远程过程调用，知道想调用哪个方法的时候调用invoke()方法
        String res = helloService.hello(object);
        System.out.println(res);
    }
}
