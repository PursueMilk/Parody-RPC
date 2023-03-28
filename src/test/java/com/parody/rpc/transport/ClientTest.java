package com.parody.rpc.transport;

import com.parody.rpc.message.RpcRequest;
import com.parody.rpc.protocol.MessageHeader;
import com.parody.rpc.protocol.MessageProtocol;
import com.parody.rpc.transport.client.NettyRpcClient;
import com.parody.rpc.transport.client.RequestMetaData;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;

public class ClientTest {
    @Test
    public void clientSendTest() throws InterruptedException {
        RequestMetaData metaData = RequestMetaData.builder()
                .address(new InetSocketAddress("127.0.0.1", 9000)).build();
        MessageProtocol<RpcRequest> message = new MessageProtocol<>();
        message.setHeader(MessageHeader.build("JSON"));
        metaData.setProtocol(message);
        System.out.println(metaData);
        new NettyRpcClient().sendRequest(metaData);
    }
}
