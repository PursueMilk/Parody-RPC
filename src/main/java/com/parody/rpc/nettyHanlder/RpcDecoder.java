package com.parody.rpc.nettyHanlder;

import com.parody.rpc.exception.RpcException;
import com.parody.rpc.message.HeartBeatMessage;
import com.parody.rpc.message.RpcRequest;
import com.parody.rpc.message.RpcResponse;
import com.parody.rpc.protocol.MessageHeader;
import com.parody.rpc.protocol.MessageProtocol;
import com.parody.rpc.protocol.MsgType;
import com.parody.rpc.protocol.ProtocolConstants;
import com.parody.rpc.serialization.RpcSerialization;
import com.parody.rpc.serialization.SerializationFactory;
import com.parody.rpc.serialization.SerializationTypeEnum;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * 解码
 */
@Slf4j
public class RpcDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 可读的数据小于请求头总的大小，直接丢弃
        if (in.readableBytes() < ProtocolConstants.HEADER_TOTAL_LEN) {
            return;
        }
        // 标记 ByteBuf 读指针位置
        in.markReaderIndex();
        // 魔数
        int magic = in.readInt();
        if (magic != ProtocolConstants.MAGIC) {
            throw new RpcException("magic number is illegal, " + magic);
        }
        // 获取协议版本号
        byte version = in.readByte();
        // 序列化
        byte serializeType = in.readByte();
        // 消息类型
        byte msgType = in.readByte();
        // 获取消息 ID
        CharSequence requestId = in.readCharSequence(ProtocolConstants.REQ_LEN, Charset.forName("UTF-8"));
        // 读取数据的长度
        int dataLength = in.readInt();
        // 可读的数据长度小于请求体长度，直接丢弃并重置读指针位置
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }
        // 读取数据
        byte[] data = new byte[dataLength];
        in.readBytes(data);
        //消息类型
        MsgType msgTypeEnum = MsgType.findByType(msgType);
        if (msgTypeEnum == null) {
            return;
        }
        //设置消息头
        MessageHeader header = new MessageHeader();
        header.setMagic(magic);
        header.setVersion(version);
        header.setSerialization(serializeType);
        header.setRequestId(String.valueOf(requestId));
        header.setMsgType(msgType);
        header.setMsgLen(dataLength);
        log.info("{}", header);
        //序列化实例
        RpcSerialization rpcSerialization = SerializationFactory.getRpcSerialization(SerializationTypeEnum.parseByType(serializeType));
        // 获取数据
        MessageProtocol protocol = packageMessage(msgTypeEnum, data, rpcSerialization);
        // 设置请求头
        protocol.setHeader(header);
        out.add(protocol);
    }


    /**
     * 消息体的反序列化及封装
     */
    public MessageProtocol packageMessage(MsgType msgType, byte[] data, RpcSerialization rpcSerialization) throws IOException {
        MessageProtocol protocol = new MessageProtocol<>();
        Object body = null;
        switch (msgType) {
            case REQUEST:
                body = rpcSerialization.deserialize(data, RpcRequest.class);
                break;
            case RESPONSE:
                body = rpcSerialization.deserialize(data, RpcResponse.class);
                break;
            case HEART:
                body = rpcSerialization.deserialize(data, HeartBeatMessage.class);
                break;
        }
        protocol.setBody(body);
        return protocol;
    }
}
