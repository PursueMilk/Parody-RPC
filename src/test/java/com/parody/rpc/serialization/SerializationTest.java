package com.parody.rpc.serialization;

import com.parody.rpc.message.RpcRequest;
import com.parody.rpc.message.RpcResponse;
import com.parody.rpc.protocol.MsgStatus;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;

public class SerializationTest {
    RpcRequest request = new RpcRequest();
    RpcResponse response = new RpcResponse();

    {
        request.setInterfaceName("HelloService");
        request.setMethodName("test");
        request.setParameterTypes(new Class[]{String.class});
        request.setParameters(new Object[]{"参数"});
        response.setCode(MsgStatus.SUCCESS.getCode());
        response.setData(request);
        response.setMessage("OK");
    }


    @Test
    public void JsonTest() throws IOException {
        System.out.println(request);
        JsonSerialization json = new JsonSerialization();
        //序列化
        byte[] arr = json.serialize(request);
        System.out.println(Arrays.toString(arr));
        //反序列化
        RpcRequest deser = json.deserialize(arr, RpcRequest.class);
        System.out.println(deser);
    }

    @Test
    public void ProtostuffTest() throws IOException {
        System.out.println(response);
        ProtostuffSerializer proto = new ProtostuffSerializer();
        byte[] data = proto.serialize(response);
        RpcResponse dser = proto.deserialize(data, RpcResponse.class);
        System.out.println(dser);
    }

}
