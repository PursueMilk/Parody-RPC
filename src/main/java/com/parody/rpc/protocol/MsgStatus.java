package com.parody.rpc.protocol;

import lombok.Getter;

/**
 * 请求结果
 */
public enum MsgStatus {
    SUCCESS((byte)0),
    FAIL((byte)1);

    @Getter
    private final byte code;

    MsgStatus(byte code) {
        this.code = code;
    }

    public static boolean isSuccess(byte code){
        return MsgStatus.SUCCESS.code == code;
    }

}
