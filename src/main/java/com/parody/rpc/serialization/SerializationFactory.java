package com.parody.rpc.serialization;


public class SerializationFactory {

    public static RpcSerialization getRpcSerialization(SerializationTypeEnum typeEnum) {
        switch (typeEnum) {
            case JSON:
                return new JsonSerialization();
            case PROTOSTUFF:
                return new ProtostuffSerializer();
            default:
                throw new IllegalArgumentException("serialization type is illegal");
        }
    }

}
