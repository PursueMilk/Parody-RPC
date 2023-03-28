package com.parody.rpc;

import com.parody.rpc.proxy.ClientProxy;
import com.parody.rpc.service.HelloService;
import com.parody.rpc.service.ReserveService;

public class RpcClient {

    public static void main(String[] args) {
        // 创建代理对象
        HelloService helloService = ClientProxy.getProxy(HelloService.class);
        // 执行对应的方法
        System.out.println(helloService.sayHello("World"));
        ReserveService reserveService=ClientProxy.getProxy(ReserveService.class);
        String str=reserveService.reserve("caff");
        System.out.println(str);
    }
}
