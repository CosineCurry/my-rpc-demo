package com.cosine.rpc;

import com.cosine.rpc.entity.RpcRequest;

/**
 * 客户端通用接口
 * @author cosine
 */
public interface RpcClient {
    /**
     * 向服务端发送请求
     * @param rpcRequest
     * @return 结果
     */
    Object sendRequest(RpcRequest rpcRequest);
}
