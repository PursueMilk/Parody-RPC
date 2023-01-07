package com.parody.rpc.protocol;

public class MessageProtocol<T> {


    /**
     * 协议头
     */
    private MessageHeader messageHeader;

    /**
     * 协议体
     */
    private T data;
}
