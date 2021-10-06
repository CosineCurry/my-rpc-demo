package com.cosine.rpc;
/**
 * 服务端通用接口
 * @author cosine
 */
public interface RpcServer {
    /**
     * 启动服务端
     * @param port 监听的端口
     */
    void start(int port);
}
