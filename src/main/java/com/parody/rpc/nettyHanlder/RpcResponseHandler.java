package com.parody.rpc.nettyHanlder;


import com.parody.rpc.config.RpcClientProperties;
import com.parody.rpc.message.HeartBeatMessage;
import com.parody.rpc.message.RpcRequest;
import com.parody.rpc.message.RpcResponse;
import com.parody.rpc.protocol.MessageHeader;
import com.parody.rpc.protocol.MessageProtocol;
import com.parody.rpc.protocol.MsgStatus;
import com.parody.rpc.protocol.MsgType;
import com.parody.rpc.transport.client.LocalRpcResponseCache;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * 数据响应处理器
 */
@Slf4j
public class RpcResponseHandler extends SimpleChannelInboundHandler<MessageProtocol<RpcResponse>> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessageProtocol<RpcResponse> rpcResponseMessageProtocol) throws Exception {
        String requestId = rpcResponseMessageProtocol.getHeader().getRequestId();
        // 收到响应 设置响应数据
        LocalRpcResponseCache.fillResponse(requestId, rpcResponseMessageProtocol);
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                // 封装心跳包
                MessageProtocol<HeartBeatMessage> messageProtocol = new MessageProtocol<>();
                MessageHeader header = MessageHeader.build(RpcClientProperties.getProperties().getSerialization());
                header.setMsgType(MsgType.HEART.getType());
                HeartBeatMessage body = new HeartBeatMessage();
                messageProtocol.setHeader(header);
                messageProtocol.setBody(body);
                log.info("向服务端：{}发送心跳包：{}", ctx.channel().remoteAddress(), messageProtocol);
                ctx.writeAndFlush(messageProtocol);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
