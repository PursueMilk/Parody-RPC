package com.parody.rpc.transport.server;


import com.parody.rpc.message.RpcResponse;
import com.parody.rpc.nettyHanlder.RpcDecoder;
import com.parody.rpc.nettyHanlder.RpcEncoder;
import com.parody.rpc.nettyHanlder.RpcRequestHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NettyRpcServer implements RpcServer {

    /**
     * 服务端启动
     * @param port
     */
    @Override
    public void start(String ip,int port) {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup work = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, work)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(new IdleStateHandler(30,0,0, TimeUnit.SECONDS))
                                    //协议编码
                                    .addLast(new RpcDecoder<RpcResponse>())
                                    //协议解码
                                    .addLast(new RpcEncoder())
                                    //请求处理器
                                    .addLast(new RpcRequestHandler());
                        }
                    })
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            //开启监听
            ChannelFuture channelFuture = bootstrap.bind(ip, port).sync();
            log.info("server address {} started on port {}", ip, port);
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            boss.shutdownGracefully();
            work.shutdownGracefully();
        }
    }
}
