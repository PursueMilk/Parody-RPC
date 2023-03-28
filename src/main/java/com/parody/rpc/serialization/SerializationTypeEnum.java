package com.parody.rpc.serialization;

import lombok.Getter;

/**
 * 序列化对应的枚举对象
 */
public enum SerializationTypeEnum {

    JSON((byte) 0),
    PROTOSTUFF((byte) 1);

    @Getter
    private byte type;

    SerializationTypeEnum(byte type) {
        this.type = type;
    }

    public static SerializationTypeEnum parseByName(String typeName) {
        for (SerializationTypeEnum typeEnum : SerializationTypeEnum.values()) {
            if (typeEnum.name().equalsIgnoreCase(typeName)) {
                return typeEnum;
            }
        }
        return JSON;
    }

    public static SerializationTypeEnum parseByType(byte type) {
        for (SerializationTypeEnum typeEnum : SerializationTypeEnum.values()) {
            if (typeEnum.getType() == type) {
                return typeEnum;
            }
        }
        return JSON;
    }

}
