package com.cosine.rpc.socket.server;

import com.cosine.rpc.RequestHandler;
import com.cosine.rpc.entity.RpcRequest;
import com.cosine.rpc.entity.RpcResponse;
import com.cosine.rpc.registry.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @Description: 处理RpcRequest的工作线程
 * @Author: cosine
 * @Date: 2021/10/4 8:01 下午
 * @Version: 1.0.0
 */
public class RequestHandlerThread implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandlerThread.class);

    /** socket网络连接 */
    private Socket socket;
    /** 请求处理类 */
    private RequestHandler requestHandler;
    /** 服务注册表 */
    private ServiceRegistry serviceRegistry;

    public RequestHandlerThread(Socket socket, RequestHandler requestHandler, ServiceRegistry serviceRegistry) {
        this.socket = socket;
        this.requestHandler = requestHandler;
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void run() {
        try {
            // 读取调用端socket的对象并解析
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
            // 在服务注册表中获取到服务
            Object service = serviceRegistry.getService(rpcRequest.getInterfaceName());
            // 利用处理类拿到返回的对象（处理类是通过反射拿到的）
            Object returnObj = requestHandler.handle(rpcRequest, service);
            // 输出流，写到输出的缓存
            objectOutputStream.writeObject(RpcResponse.success(returnObj));
            // 强制将缓冲区中的数据发送出去,不必等到缓冲区满
            objectOutputStream.flush();

        } catch (IOException | ClassNotFoundException e) {
            logger.error("调用时有错误发生：" + e);
        }
    }
}
