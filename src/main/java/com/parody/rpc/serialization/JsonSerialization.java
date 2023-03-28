package com.parody.rpc.serialization;

import com.alibaba.fastjson.JSON;
import com.parody.rpc.message.RpcRequest;

import java.io.IOException;

/**
 * Json序列化
 */
public class JsonSerialization implements RpcSerialization {

    @Override
    public <T> byte[] serialize(T obj) throws IOException {
        //将Java对象序列化为JSON字符串，返回JSON字符串的utf-8 bytes
        return JSON.toJSONBytes(obj);
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clz) throws IOException {
        T obj = JSON.parseObject(data, clz);
        if (obj instanceof RpcRequest) {
            handleRequest(obj);
        }
        return obj;
    }


    /**
     * 这里由于使用JSON序列化和反序列化Object中的数组，无法保证反序列化后仍然为原实例类型需要重新判断处理
     */
    private void handleRequest(Object obj) {
        RpcRequest rpcRequest = (RpcRequest) obj;
        for (int i = 0; i < rpcRequest.getParameterTypes().length; i++) {
            Class<?> clazz = rpcRequest.getParameterTypes()[i];
            //判断该类是否为参数类型或者父类，不为则反序列化出错
            if (!clazz.isAssignableFrom(rpcRequest.getParameters()[i].getClass())) {
                //对参数进行反序列化修正
                byte[] bytes = JSON.toJSONBytes(rpcRequest.getParameters()[i]);
                rpcRequest.getParameters()[i] = JSON.parseObject(bytes, clazz);
            }
        }
    }


}
