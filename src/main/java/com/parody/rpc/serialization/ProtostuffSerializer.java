package com.parody.rpc.serialization;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ProtostuffSerializer implements RpcSerialization {

    private LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);

    private Map<Class<?>, Schema<?>> schemaCache = new ConcurrentHashMap<>();


    @Override
    public <T> byte[] serialize(T obj) throws IOException {
        Class clazz = obj.getClass();
        Schema schema = getSchema(clazz);
        byte[] data;
        try {
            data = ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } finally {
            buffer.clear();
        }
        return data;
    }


    @Override
    public <T> T deserialize(byte[] data, Class<T> clz) throws IOException {
        Schema schema = getSchema(clz);
        Object obj = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(data, obj, schema);
        return (T) obj;
    }


    private Schema getSchema(Class clazz) {
        Schema schema = schemaCache.get(clazz);
        if (Objects.isNull(schema)) {
            schema = RuntimeSchema.getSchema(clazz);
            if (Objects.nonNull(schema)) {
                schemaCache.put(clazz, schema);
            }
        }
        return schema;
    }
}
