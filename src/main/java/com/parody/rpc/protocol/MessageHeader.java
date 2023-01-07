package com.parody.rpc.protocol;

public class MessageHeader {

    /**
     * 魔数
     */
    private short magic;


    /**
     * 协议版本号
     */
    private String version;


    /**
     * 序列化方式
     */
    private byte serialization;

    /**
     * 报文类型
     */
    private byte status;

    /**
     * 消息ID
     */
    private String requestId;


    /**
     * 数据长度
     */
    private int msgLen;


}
