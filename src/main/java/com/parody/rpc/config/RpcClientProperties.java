package com.parody.rpc.config;

import lombok.Data;

/**
 * 配置类
 */
@Data
public class RpcClientProperties {

    /**
     * 负载均衡
     */
    private String balance = "RANDOM";

    /**
     * 序列化
     */
    private String serialization = "JSON";

    /**
     * 服务发现地址
     */
    private String discoveryAddress = "127.0.0.1:8848";

    /**
     * 服务调用超时（毫秒）
     */
    private Integer timeout = 3000;



    // 单例
    private static RpcClientProperties rpcClientProperties = new RpcClientProperties();

    private RpcClientProperties() {}

    // 加载配置类
    static {
        try {
            RpcClientConfig.fillProperties(rpcClientProperties);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取配置类对象
     */
    public static RpcClientProperties getProperties() {
        return rpcClientProperties;
    }
}
