package com.parody.rpc.nacos;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.parody.rpc.utils.NacosUtils;

import java.net.InetSocketAddress;

public class NacosServerDiscovery implements ServerDiscovery {

    @Override
    public InetSocketAddress serverDiscovery(String serverName) {
        try {
            Instance instance = NacosUtils.getInstance(serverName);
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }
}
