package com.parody.rpc.proxy;

import com.parody.rpc.annotation.RpcScanner;
import com.parody.rpc.annotation.RpcService;
import com.parody.rpc.exception.RpcException;
import com.parody.rpc.nacos.NacosServerRegister;
import com.parody.rpc.nacos.ServerRegister;
import com.parody.rpc.transport.server.LocalServerCache;
import com.parody.rpc.transport.server.NettyRpcServer;
import com.parody.rpc.transport.server.RpcServer;
import com.parody.rpc.utils.PackageScanUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Set;

@Slf4j
public class ServerManager {

    // 通信服务
    private RpcServer rpcServer;

    private String ip;

    private Integer port;

    //服务注册
    private ServerRegister register;


    public ServerManager(String ip, Integer port) {
        // Netty 通信
        this.rpcServer = new NettyRpcServer();
        // 未指定 ip 地址，默认本地地址
        if (ip == null) {
            try {
                //获取本地的ip地址
                this.ip = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        } else {
            this.ip = ip;
        }
        this.port = port;
        // Nacos 服务注册
        register = new NacosServerRegister();
    }

    public ServerManager(Integer port) {
        this(null, port);
    }

    /**
     * 服务端启动
     */
    public void start() {
        // 服务注册
        autoRegistry();
        // Netty 通信启动
        rpcServer.start(ip, port);
    }


    /**
     * 服务注册
     */
    private void autoRegistry() {
        //获取启动类的路径
        String mainClassPath = PackageScanUtils.getStackTrace();
        Class<?> mainClass;
        try {
            //获取启动类的类对象
            mainClass = Class.forName(mainClassPath);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("启动类为找到");
        }
        if (!mainClass.isAnnotationPresent(RpcScanner.class)) {
            throw new RpcException("启动类缺少@RpcScanner 注解");
        }
        String annotationValue = mainClass.getAnnotation(RpcScanner.class).value();
        //如果注解路径的值是空，默认扫描路径为 Main 所在的包路径
        if ("".equals(annotationValue)) {
            annotationValue = mainClassPath.substring(0, mainClassPath.lastIndexOf("."));
        }
        //获取所有类的类对象的 set 集合
        Set<Class<?>> set = PackageScanUtils.getClasses(annotationValue);
        // 判断扫描路径上的类是否有 RpcService 类
        for (Class<?> c : set) {
            // @RpcService注解的类才能注册
            if (c.isAnnotationPresent(RpcService.class)) {
                RpcService service = c.getAnnotation(RpcService.class);
                // 获取注册中心的注册名
                String serverName = service.name();
                Object object;
                try {
                    //创建实例
                    object = c.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    log.error("{}", e);
                    log.error("创建对象{}发生错误", c);
                    continue;
                }
                if ("".equals(serverName)) {
                    // 注解的值如果为空，使用其继承接口的全限定类名（包名 + 类名）
                    Class<?>[] interfaces = c.getInterfaces();
                    for (Class clz : interfaces) {
                        addServer(object, clz.getCanonicalName());
                    }
                } else {
                    addServer(object, serverName);
                }
            }
        }
    }

    private void addServer(Object object, String serverName) {
        // 添加服务实例对象到本地缓存
        LocalServerCache.add(serverName, object);
        // 将服务注册到 Nacos 中
        register.serverRegister(serverName, new InetSocketAddress(ip, port));
    }
}
