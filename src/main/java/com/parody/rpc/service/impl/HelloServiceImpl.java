package com.parody.rpc.service.impl;


import com.parody.rpc.annotation.RpcService;
import com.parody.rpc.service.HelloService;

@RpcService
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String name) {
        return "Hello, " + name;
    }
}
