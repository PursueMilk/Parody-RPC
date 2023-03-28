package com.parody.rpc.serialization;

import com.parody.rpc.exception.RpcException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.parody.rpc.serialization.SerializationTypeEnum.JSON;
import static com.parody.rpc.serialization.SerializationTypeEnum.PROTOSTUFF;

/**
 * 序列化实例工厂
 */
public class SerializationFactory {

    private static final Map<SerializationTypeEnum, RpcSerialization> map = new ConcurrentHashMap<>();

    static {
        map.put(JSON, new JsonSerialization());
        map.put(PROTOSTUFF, new ProtostuffSerializer());
    }

    public static RpcSerialization getRpcSerialization(SerializationTypeEnum typeEnum) {
        RpcSerialization serialization = map.get(typeEnum);
        if (serialization == null) {
            throw new RpcException("serialization type is illegal");
        }
        return serialization;
    }
}
