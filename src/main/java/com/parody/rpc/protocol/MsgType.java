package com.parody.rpc.protocol;

import lombok.Getter;

/**
 * 消息类型
 */
public enum MsgType {
    REQUEST((byte) 1),
    RESPONSE((byte) 2),
    HEART((byte) 3);

    @Getter
    private final byte type;

    MsgType(byte type) {
        this.type = type;
    }


    /**
     * 根据类型获取消息类型
     */
    public static MsgType findByType(byte type) {
        for (MsgType msgType : MsgType.values()) {
            if (msgType.getType() == type) {
                return msgType;
            }
        }
        return null;
    }
}
