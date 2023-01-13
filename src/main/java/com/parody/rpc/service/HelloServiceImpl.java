package com.parody.rpc.service;


import com.parody.rpc.annotation.RpcService;

@RpcService(interfaceType = HelloService.class)
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String name) {
        return "你好, " + name;
    }
}
