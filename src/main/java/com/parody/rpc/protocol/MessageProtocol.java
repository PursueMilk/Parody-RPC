package com.parody.rpc.protocol;

import lombok.Data;

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
