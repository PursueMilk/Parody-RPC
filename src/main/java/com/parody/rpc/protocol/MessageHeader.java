package com.parody.rpc.protocol;

import com.parody.rpc.serialization.SerializationTypeEnum;
import lombok.Data;

import java.util.UUID;

@Data
public class MessageHeader {

    /**
     * 魔数
     */
    private int magic;


    /**
     * 协议版本号
     */
    private byte version;


    /**
     * 序列化方式
     */
    private byte serialization;

    /**
     * 报文类型
     */
    private byte msgType;

    /**
     * 消息ID
     */
    private String requestId;


    /**
     * 数据长度
     */
    private int msgLen;


    /**
     * 初始化请求头
     */
    public static MessageHeader build(String serialization) {
        //初始化请求头
        MessageHeader messageHeader = new MessageHeader();
        messageHeader.setMagic(ProtocolConstants.MAGIC);
        messageHeader.setVersion(ProtocolConstants.VERSION);
        messageHeader.setRequestId(UUID.randomUUID().toString().replaceAll("-", ""));
        messageHeader.setMsgType(MsgType.REQUEST.getType());
        messageHeader.setSerialization(SerializationTypeEnum.parseByName(serialization).getType());
        return messageHeader;
    }

}
