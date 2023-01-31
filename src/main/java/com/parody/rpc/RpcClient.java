package com.parody.rpc;

import com.parody.rpc.proxy.ClientProxy;
import com.parody.rpc.service.HelloService;

public class RpcClient {

    public static void main(String[] args) {
        //创建代理对象
        HelloService helloService = ClientProxy.getProxy(HelloService.class);
        System.out.println(helloService.sayHello("World"));
    }
}
