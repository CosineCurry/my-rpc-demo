package com.cosine.rpc.socket.server;

import com.cosine.rpc.RequestHandler;
import com.cosine.rpc.RpcServer;
import com.cosine.rpc.registry.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * @Description: 利用socket实现的服务端
 * @Author: cosine
 * @Date: 2021/10/5 8:59 下午
 * @Version: 1.0.0
 */
public class SocketServer implements RpcServer {
    public static final Logger logger = LoggerFactory.getLogger(SocketServer.class);

    /** 线程池相关 */
    private static final int CORE_POOL_SIZE = 5;
    private static final int MAXIMUM_POOL_SIZE = 50;
    private static final int KEEP_ALIVE_TIME = 60;
    private static final int BLOCKING_QUEUE_CAPACITY = 100;
    private final ExecutorService threadPool;

    private RequestHandler requestHandler = new RequestHandler();
    private final ServiceRegistry serviceRegistry;

    public SocketServer(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
        threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY), Executors.defaultThreadFactory());
    }

    @Override
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
