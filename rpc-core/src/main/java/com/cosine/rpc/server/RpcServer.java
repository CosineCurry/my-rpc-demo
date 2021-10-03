package com.cosine.rpc.server;

import com.cosine.rpc.client.RpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * @Description: 远程方法调用的提供者（服务端）
 *               BIO的网络编程模型（Tomcat），需要有一个线程池，以及服务端的socket进行监听
 * @Author: cosine
 * @Date: 2021/10/2 4:02 下午
 * @Version: 1.0.0
 */
public class RpcServer {
    public static final Logger logger = LoggerFactory.getLogger(RpcServer.class);

    private final ExecutorService threadPool;

    public RpcServer() {
        // 核心线程数量
        int corePoolSize = 5;
        // 最大线程数量
        int maximumPoolSize = 50;
        // 非核心线程存活时间
        long keepAliveTime = 60;
        // 工作队列
        BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<>(100);
        threadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workingQueue, Executors.defaultThreadFactory());
    }

    public void register(Object service, int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            logger.info("服务器正在启动...");
            Socket socket;
            // BIO的网络模型，来一个socket连接就创建一个新的工作线程
            while ((socket = serverSocket.accept()) != null) {
                logger.info("客户端连接成功！IP为："+ socket.getInetAddress());
                // 给到一个工作线程
                threadPool.execute(new WorkerThread(service, socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
