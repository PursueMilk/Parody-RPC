package com.parody.rpc.protocol;

import lombok.Data;

/**
 * 通信协议
 */
@Data
public class MessageProtocol<T> {


    /**
     * 协议头
     */
    private MessageHeader header;

    /**
     * 协议体
     */
    private T body;
}
