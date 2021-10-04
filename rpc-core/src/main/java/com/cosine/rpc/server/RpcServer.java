package com.cosine.rpc.server;

import com.cosine.rpc.registry.ServiceRegistry;
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

    /** 线程池相关 */
    private static final int CORE_POOL_SIZE = 5;
    private static final int MAXIMUM_POOL_SIZE = 50;
    private static final int KEEP_ALIVE_TIME = 60;
    private static final int BLOCKING_QUEUE_CAPACITY = 100;
    private final ExecutorService threadPool;

    private RequestHandler requestHandler = new RequestHandler();
    private final ServiceRegistry serviceRegistry;

    public RpcServer(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
        threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY), Executors.defaultThreadFactory());
    }

    /**
     * @Description 启动服务器，监听某个端口
     * @Param [port]
     * @return void
     * @Author cosine
     * @Date 2021/10/4
     */
    public void start(int port) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            logger.info("服务器正在启动...");
            Socket socket;
            // BIO的网络模型，来一个socket连接就创建一个新的工作线程
            while ((socket = serverSocket.accept()) != null) {
                logger.info("消费者连接: {}:{}", socket.getInetAddress(), socket.getPort());
                // 给到一个工作线程
                threadPool.execute(new RequestHandlerThread(socket, requestHandler, serviceRegistry));
            }
            // 关闭线程池
            threadPool.shutdown();
        } catch (IOException e) {
            logger.error("服务器启动时有错误发生:", e);
        }

    }
}
