package com.parody.rpc.transport.client;

import com.parody.rpc.message.RpcRequest;
import com.parody.rpc.message.RpcResponse;
import com.parody.rpc.nettyHanlder.RpcDecoder;
import com.parody.rpc.nettyHanlder.RpcEncoder;
import com.parody.rpc.nettyHanlder.RpcResponseHandler;
import com.parody.rpc.protocol.MessageProtocol;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


@Slf4j
public class NettyRpcClient implements RpcClient {

    // 客户端引导类加载器
    private static final Bootstrap bootstrap;

    // 线程组
    private static final EventLoopGroup eventLoopGroup;

    // 缓存的连接通道
    private static final Map<String, Channel> channels = new ConcurrentHashMap<>();


    static {
        bootstrap = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast(new IdleStateHandler(0, 15, 0, TimeUnit.SECONDS))
                                //解码
                                .addLast(new RpcDecoder())
                                //编码
                                .addLast(new RpcEncoder<RpcRequest>())
                                //响应处理
                                .addLast(new RpcResponseHandler());
                    }
                });
    }


    /**
     * 发送请求
     */
    @Override
    public MessageProtocol sendRequest(RequestMetaData metaData) throws InterruptedException {
        // 获取发送的数据包
        MessageProtocol<RpcRequest> protocol = metaData.getProtocol();
        // 接收响应的结果
        RpcFuture<MessageProtocol<RpcResponse>> future = new RpcFuture<>();
        // 将接收结果的Future注册到Map中
        LocalRpcResponseCache.add(protocol.getHeader().getRequestId(), future);
        // 获取通道
        Channel channel = get(metaData.getAddress());
        // 发送数据
        channel.writeAndFlush(protocol);
        return metaData.getTimeout() != null ? future.get(metaData.getTimeout(), TimeUnit.MILLISECONDS) : future.get();
    }

    /**
     * 获取通道
     */
    public Channel get(InetSocketAddress inetSocketAddress) throws InterruptedException {
        // 通道对应的键
        String key = inetSocketAddress.toString();
        if (channels.containsKey(key)) {
            // 包含获取通道
            Channel channel = channels.get(key);
            // 判断通道是否存活
            if (channels != null && channel.isActive()) {
                return channel;
            } else {
                channels.remove(key);
            }
        }
        // 创建通道
        Channel channel = connect(inetSocketAddress);
        // 加入缓存
        channels.put(key, channel);
        return channel;
    }


    /**
     * 建立连接
     */
    public Channel connect(InetSocketAddress address) throws InterruptedException {
        // 异步建立连接
        ChannelFuture channelFuture = bootstrap.connect(address).sync();
        // 监听连接结果
        channelFuture.addListener((ChannelFutureListener) future -> {
            if (channelFuture.isSuccess()) {
                log.info("connect rpc server {} on port {} success.", address.getAddress(), address.getPort());
            } else {
                log.error("connect rpc server {} on port {} failed.", address.getAddress(), address.getPort());
                channelFuture.cause().printStackTrace();
                eventLoopGroup.shutdownGracefully();
            }
        });
        return channelFuture.channel();
    }


}
