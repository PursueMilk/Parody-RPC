package com.parody.rpc.exception;

/**
 * 异常类
 */
public class RpcException extends RuntimeException{

    public RpcException() {
        super();
    }

    public RpcException(String msg) {
        super(msg);
    }
}
