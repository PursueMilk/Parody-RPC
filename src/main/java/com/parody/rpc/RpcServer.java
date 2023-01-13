package com.parody.rpc;

import com.parody.rpc.annotation.RpcScanner;
import com.parody.rpc.proxy.ServerManager;

@RpcScanner(value = "com.parody.rpc.service")
public class RpcServer {
    public static void main(String[] args) {
        new ServerManager(9000).start();
    }
}
