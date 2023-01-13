package com.parody.rpc.utils;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.parody.rpc.config.RpcClientProperties;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NacosTest {

    @Test
    public void test() throws UnknownHostException {
        System.out.println(RpcClientProperties.getProperties());
        System.out.println(InetAddress.getLocalHost().getHostAddress());
    }

    @Test
    public void NacosTest() throws NacosException {
        NamingFactory.createNamingService("127.0.0.1:8848");
        /*        NacosUtils.registerServer("test",new InetSocketAddress("127.0.0.1",8848));*/
    }
}
