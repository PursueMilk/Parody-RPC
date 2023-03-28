package com.parody.rpc.nettyHanlder;


import com.parody.rpc.exception.RpcException;
import com.parody.rpc.message.RpcRequest;
import com.parody.rpc.message.RpcResponse;
import com.parody.rpc.protocol.MessageHeader;
import com.parody.rpc.protocol.MessageProtocol;
import com.parody.rpc.protocol.MsgStatus;
import com.parody.rpc.protocol.MsgType;
import com.parody.rpc.transport.server.LocalServerCache;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 处理客户端请求
 */
@Slf4j
public class RpcRequestHandler<T> extends SimpleChannelInboundHandler<MessageProtocol<T>> {

    //处理请求的线程池
    private final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(4, 7, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000));


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessageProtocol<T> rpcRequestMessageProtocol) {
        // 多线程处理每个请求
        threadPoolExecutor.submit(() -> {
            MessageHeader header = rpcRequestMessageProtocol.getHeader();
            //处理心跳包
            if (header.getMsgType() == MsgType.HEART.getType()) {
                log.info("收到客户端的心跳包");
                return;
            }
            MessageProtocol<RpcResponse> resProtocol = new MessageProtocol<>();
            RpcResponse response = new RpcResponse();
            // 设置头部消息类型为响应
            header.setMsgType(MsgType.RESPONSE.getType());
            try {
                //获得方法返回值
                Object result = handle((RpcRequest) rpcRequestMessageProtocol.getBody());
                response.setData(result);
                response.setCode(MsgStatus.SUCCESS.getCode());
                resProtocol.setHeader(header);
                resProtocol.setBody(response);
            } catch (Throwable throwable) {
                response.setCode(MsgStatus.FAIL.getCode());
                response.setMessage(throwable.toString());
                log.error("process request {} error", header.getRequestId(), throwable);
            }
            // 把数据写回去
            channelHandlerContext.writeAndFlush(resProtocol);
        });
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                log.info("长时间未收到心跳包，断开连接");
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }


    /**
     * 反射调用获取数据
     */
    private Object handle(RpcRequest request) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String key = request.getInterfaceName();
        log.info("{}", key);
        Object bean = LocalServerCache.get(key);
        if (bean == null) {
            throw new RpcException(String.format("service not exist: %s !", request.getInterfaceName()));
        }
        // 反射调用
        Method method = bean.getClass().getMethod(request.getMethodName(), request.getParameterTypes());
        return method.invoke(bean, request.getParameters());
    }

}
