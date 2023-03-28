package com.parody.rpc.transport.client;

import com.parody.rpc.message.RpcRequest;
import com.parody.rpc.protocol.MessageProtocol;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.net.InetSocketAddress;

@Data
@Builder
public class RequestMetaData implements Serializable {
    /**
     * 协议
     */
    private MessageProtocol<RpcRequest> protocol;

    /**
     * 地址
     */
    private InetSocketAddress address;


    /**
     * 服务调用超时
     */
    private Integer timeout;
}
