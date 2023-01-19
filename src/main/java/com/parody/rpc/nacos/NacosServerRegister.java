package com.parody.rpc.nacos;

import com.alibaba.nacos.api.exception.NacosException;
import com.parody.rpc.utils.NacosUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class NacosServerRegister implements ServerRegister{
    @Override
    public void serverRegister(String serverName, InetSocketAddress address) {
        try {
            NacosUtils.registerServer(serverName,address);
            log.info("注册{}，地址:{}",serverName,address);
        } catch (NacosException e) {
            throw new RuntimeException("nacos注册异常");
        }
    }
}
