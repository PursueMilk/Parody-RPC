package com.parody.rpc.transport.client;

import com.parody.rpc.protocol.MessageProtocol;


public interface RpcClient {

    MessageProtocol sendRequest(RequestMetaData requestMetaData) throws InterruptedException;
}
