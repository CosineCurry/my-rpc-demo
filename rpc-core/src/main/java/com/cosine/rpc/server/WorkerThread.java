package com.cosine.rpc.server;

import entity.RpcRequest;
import entity.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

/**
 * @Description: 服务端的工作线程
 * @Author: cosine
 * @Date: 2021/10/2 4:28 下午
 * @Version: 1.0.0
 */
public class WorkerThread implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(WorkerThread.class);

    private Object service;
    private Socket socket;

    public WorkerThread(Object service, Socket socket) {
        this.service = service;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            // 读取调用端socket的对象并解析
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
            // 利用反射拿到对象的方法，这边的service对象是HelloService的代理对象，method是hello方法
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            // 这边returnObj是拿到了服务端的hello方法的返回值
            Object returnObj = method.invoke(service, rpcRequest.getParameters());
            logger.info("服务端 returnObj:"+returnObj.toString());
            // 输出流，写到输出的缓存
            objectOutputStream.writeObject(RpcResponse.success(returnObj));
            // 强制将缓冲区中的数据发送出去,不必等到缓冲区满
            objectOutputStream.flush();

        } catch (IOException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            logger.error("调用时有错误发生："+e);
        }
    }
}
