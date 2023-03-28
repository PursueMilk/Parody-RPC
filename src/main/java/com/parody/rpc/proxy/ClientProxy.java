package com.parody.rpc.proxy;

import com.parody.rpc.config.RpcClientProperties;
import com.parody.rpc.exception.RpcException;
import com.parody.rpc.message.RpcRequest;
import com.parody.rpc.message.RpcResponse;
import com.parody.rpc.nacos.NacosServerDiscovery;
import com.parody.rpc.nacos.ServerDiscovery;
import com.parody.rpc.protocol.*;
import com.parody.rpc.transport.client.NettyRpcClient;
import com.parody.rpc.transport.client.RequestMetaData;
import com.parody.rpc.transport.client.RpcClient;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;

/**
 * 代理类
 */
@Slf4j
public class ClientProxy implements InvocationHandler {


    // Nacos 服务发现
    private ServerDiscovery discovery;


    // 配置类
    private RpcClientProperties properties;


    // 接口类对象
    private Class<?> clazz;

    // 服务通信
    private RpcClient client;


    /**
     * 获取接口的代理对象
     */
    public static <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, new ClientProxy(clazz));
    }

    public ClientProxy(Class<?> clazz) {
        this.clazz = clazz;
        discovery = new NacosServerDiscovery();
        properties = RpcClientProperties.getProperties();
        client = new NettyRpcClient();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 服务名称
        String serverName = clazz.getCanonicalName();
        // 服务端地址
        InetSocketAddress address = discovery.serverDiscovery(serverName);
        // 填充数据包
        MessageProtocol<RpcRequest> message = fillPackage(method, args);
        //设置其他信息
        RequestMetaData metaData = RequestMetaData.builder()
                .protocol(message)
                .address(address)
                .timeout(properties.getTimeout())
                .build();
        log.info("{}", metaData);
        // 发送网络请求 拿到结果
        MessageProtocol<RpcResponse> responseMessage = client.sendRequest(metaData);
        if (responseMessage == null) {
            log.error("请求超时");
            throw new RpcException("rpc调用结果失败， 请求超时 timeout:" + properties.getTimeout());
        }
        if (!MsgStatus.isSuccess(responseMessage.getBody().getCode())) {
            log.error("rpc调用结果失败， message：{}", responseMessage.getBody().getMessage());
            throw new RpcException(responseMessage.getBody().getMessage());
        }
        return responseMessage.getBody().getData();
    }


    /**
     * 设置发送的数据包
     */
    public MessageProtocol<RpcRequest> fillPackage(Method method, Object[] args) {
        MessageProtocol<RpcRequest> message = new MessageProtocol<>();
        //设置请求头
        message.setHeader(MessageHeader.build(properties.getSerialization()));
        //设置请求体
        RpcRequest request = new RpcRequest();
        //接口名
        request.setInterfaceName(clazz.getCanonicalName());
        //方法名
        request.setMethodName(method.getName());
        //参数类型
        request.setParameterTypes(method.getParameterTypes());
        //参数值
        request.setParameters(args);
        message.setBody(request);
        return message;
    }


}
