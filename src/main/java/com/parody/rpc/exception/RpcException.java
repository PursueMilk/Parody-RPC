package com.parody.rpc.exception;

public class RpcException extends RuntimeException{

    public RpcException() {
        super();
    }

    public RpcException(String msg) {
        super(msg);
    }
}
