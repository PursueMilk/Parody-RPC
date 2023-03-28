package com.parody.rpc.utils;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.parody.rpc.balancer.LoadBalance;
import com.parody.rpc.balancer.LoadBalanceFactory;
import com.parody.rpc.config.RpcClientProperties;
import com.parody.rpc.exception.RpcException;
import io.protostuff.Rpc;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Nacos 工具类
 */
public class NacosUtils {

    // Nacos 操作对象
    private static final NamingService namingService;

    // 服务端注册的服务名
    private static final Set<String> serviceNames = new HashSet<>();

    // 服务端地址
    private static InetSocketAddress address;

    // 配置类
    private static final RpcClientProperties properties;

    static {
        properties = RpcClientProperties.getProperties();
        namingService = getNacosNamingService();
    }

    //初始化
    public static NamingService getNacosNamingService() {
        try {
            return NamingFactory.createNamingService(properties.getDiscoveryAddress());
        } catch (NacosException e) {
            throw new RuntimeException("连接到Nacos时发生错误");
        }
    }

    /**
     * 注册服务
     *
     * @param serverName
     * @param address
     * @throws NacosException
     */
    public static void registerServer(String serverName, InetSocketAddress address) throws NacosException {
        namingService.registerInstance(serverName, address.getHostName(), address.getPort());
        NacosUtils.address = address;
        serviceNames.add(serverName);
    }


    /**
     * 根据负载均衡算法获取当前服务名中的实例
     * @param serverName
     * @return
     * @throws NacosException
     */
    public static Instance getInstance(String serverName) throws NacosException {
        List<Instance> list = namingService.getAllInstances(serverName);
        LoadBalance balance = LoadBalanceFactory.getLoadBalance(properties.getBalance());
        return balance.getInstance(list);
    }

    /**
     * 注销服务
     */
    public static void clearRegister() {
        if (!serviceNames.isEmpty() && address != null) {
            String host = address.getHostName();
            int port = address.getPort();
            Iterator<String> iterator = serviceNames.iterator();
            while (iterator.hasNext()) {
                String serviceName = iterator.next();
                try {
                    namingService.deregisterInstance(serviceName, host, port);
                } catch (NacosException e) {
                    new RpcException("注销服务失败");
                }
            }
        }
    }

}
