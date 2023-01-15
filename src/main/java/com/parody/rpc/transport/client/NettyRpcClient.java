package com.parody.rpc.transport.client;

import com.parody.rpc.message.RpcRequest;
import com.parody.rpc.message.RpcResponse;
import com.parody.rpc.nettyHanlder.RpcDecoder;
import com.parody.rpc.nettyHanlder.RpcEncoder;
import com.parody.rpc.nettyHanlder.RpcResponseHandler;
import com.parody.rpc.protocol.MessageProtocol;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;


@Slf4j
public class NettyRpcClient implements RpcClient {

    private final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup;

    public NettyRpcClient() {
        bootstrap = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup(4);
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast(new IdleStateHandler(0, 15, 0, TimeUnit.SECONDS))
                                //编码
                                .addLast(new RpcDecoder<RpcRequest>())
                                //解码
                                .addLast(new RpcEncoder())
                                //响应处理
                                .addLast(new RpcResponseHandler());
                    }
                });
    }

    /**
     * 发送请求
     * @param metaData
     * @return
     * @throws InterruptedException
     */
    @Override
    public MessageProtocol sendRequest(RequestMetaData metaData) throws InterruptedException {
        MessageProtocol<RpcRequest> protocol = metaData.getProtocol();
        //接收响应的结果
        RpcFuture<MessageProtocol<RpcResponse>> future = new RpcFuture<>();
        LocalRpcResponseCache.add(protocol.getHeader().getRequestId(), future);
        // TCP 连接
        ChannelFuture channelFuture = bootstrap.connect(metaData.getAddress(), metaData.getPort()).sync();
        channelFuture.addListener((ChannelFutureListener) arg0 -> {
            if (channelFuture.isSuccess()) {
                log.info("connect rpc server {} on port {} success.", metaData.getAddress(), metaData.getPort());
            } else {
                log.error("connect rpc server {} on port {} failed.", metaData.getAddress(), metaData.getPort());
                channelFuture.cause().printStackTrace();
                eventLoopGroup.shutdownGracefully();
            }
        });
        //TODO 连接复用 写入数据
        channelFuture.channel().writeAndFlush(protocol);
        return metaData.getTimeout() != null ? future.get(metaData.getTimeout(), TimeUnit.MILLISECONDS) : future.get();
    }
}
