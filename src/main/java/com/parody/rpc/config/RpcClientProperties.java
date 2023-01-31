package com.parody.rpc.config;

import lombok.Data;

/**
 * 配置类采用单例模式
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


    /**
     * 服务版本号
     */
    private String version = "1.0";


    private static RpcClientProperties rpcClientProperties = new RpcClientProperties();


    private RpcClientProperties() {}

    static {
        try {
            //加载自定义配置
            RpcClientConfig.fillProperties(rpcClientProperties);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取配置类对象
     *
     * @return
     */
    public static RpcClientProperties getProperties() {
        return rpcClientProperties;
    }
}
