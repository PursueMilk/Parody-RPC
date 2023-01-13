package com.parody.rpc;

import com.parody.rpc.proxy.ClientProxy;
import com.parody.rpc.service.HelloService;

public class RpcClient {

    public static void main(String[] args) {
        HelloService helloService = ClientProxy.getProxy(HelloService.class);
        System.out.println(helloService.sayHello("小明"));
    }
}
