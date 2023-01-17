package com.parody.rpc.proxy;

import com.parody.rpc.config.RpcClientProperties;
import com.parody.rpc.exception.RpcException;
import com.parody.rpc.message.RpcRequest;
import com.parody.rpc.message.RpcResponse;
import com.parody.rpc.nacos.NacosServerDiscovery;
import com.parody.rpc.nacos.ServerDiscovery;
import com.parody.rpc.protocol.MessageHeader;
import com.parody.rpc.protocol.MessageProtocol;
import com.parody.rpc.protocol.MsgStatus;
import com.parody.rpc.transport.client.NettyRpcClient;
import com.parody.rpc.transport.client.RequestMetaData;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;


@Slf4j
public class ClientProxy implements InvocationHandler {

    private ServerDiscovery discovery;

    private RpcClientProperties properties;

    private Class<?> clazz;


    /**
     * 获取接口的代理对象
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, new ClientProxy(clazz));
    }

    public ClientProxy(Class<?> clazz) {
        this.clazz = clazz;
        discovery = new NacosServerDiscovery();
        properties = RpcClientProperties.getProperties();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        InetSocketAddress address=discovery.serverDiscovery(clazz.getSimpleName()+"-"+properties.getVersion());
        MessageProtocol<RpcRequest> messageProtocol = new MessageProtocol<>();
        //设置请求头
        messageProtocol.setHeader(MessageHeader.build(properties.getSerialization()));
        //设置请求体
        RpcRequest request = new RpcRequest();
        request.setInterfaceName(clazz.getSimpleName());
        request.setMethodName(method.getName());
        request.setVersion(properties.getVersion());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);
        messageProtocol.setBody(request);
        //设置其他信息
        RequestMetaData metaData = RequestMetaData.builder()
                .protocol(messageProtocol)
                .address(address.getAddress().getHostAddress())
                .port(address.getPort())
                .timeout(properties.getTimeout())
                .build();
        log.info("{}",metaData);
        // 发送网络请求 拿到结果
        MessageProtocol<RpcResponse> responseMessageProtocol = new NettyRpcClient().sendRequest(metaData);
        if (responseMessageProtocol == null) {
            log.error("请求超时");
            throw new RpcException("rpc调用结果失败， 请求超时 timeout:" + properties.getTimeout());
        }
        if (!MsgStatus.isSuccess(responseMessageProtocol.getBody().getCode())) {
            log.error("rpc调用结果失败， message：{}", responseMessageProtocol.getBody().getMessage());
            throw new RpcException(responseMessageProtocol.getBody().getMessage());
        }
        return responseMessageProtocol.getBody().getData();
    }
}
