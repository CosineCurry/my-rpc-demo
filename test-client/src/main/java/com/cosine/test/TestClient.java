package com.cosine.test;

import com.cosine.rpc.api.HelloObject;
import com.cosine.rpc.api.HelloService;
import com.cosine.rpc.client.RpcClientProxy;

/**
 * @Description: 测试客户端
 * @Author: cosine
 * @Date: 2021/10/2 8:33 下午
 * @Version: 1.0.0
 */
public class TestClient {

    public static void main(String[] args) {
        RpcClientProxy proxy = new RpcClientProxy("127.0.0.1", 9001);
        // 代理共有接口类 HelloService类
        HelloService helloService = proxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(13, "This is a message");
        // 远程过程调用，知道想调用哪个方法的时候调用invoke()方法
        String res = helloService.hello(object);
        System.out.println(res);
    }
}
