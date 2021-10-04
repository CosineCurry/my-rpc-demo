package com.cosine.rpc.exception;

import com.cosine.rpc.enumeration.RpcError;

/**
 * @Description: RPC调用过程的异常
 * @Author: cosine
 * @Date: 2021/10/4 3:52 下午
 * @Version: 1.0.0
 */
public class RpcException extends RuntimeException {

    public RpcException(RpcError error, String detail) {
        super(error.getMessage() + ":" + detail);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(RpcError error) {
        super(error.getMessage());
    }
}
