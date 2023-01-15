package com.parody.rpc.transport.server;


public interface RpcServer {
    /**
     * 开启服务
     * @param port
     */
    void start(String ip,int port);
}
