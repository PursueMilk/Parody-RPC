package com.parody.rpc.proxy;

import com.parody.rpc.annotation.RpcScanner;
import com.parody.rpc.annotation.RpcService;
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

    private RpcServer rpcServer;

    private String ip;

    private Integer port;

    //服务注册
    private ServerRegister register;


    public ServerManager(String ip, Integer port) {
        this.rpcServer = new NettyRpcServer();
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
        register = new NacosServerRegister();
        autoRegistry();
    }

    public ServerManager(Integer port) {
        this(null, port);
    }

    public void start() {
        rpcServer.start(ip, port);
    }


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
            throw new RuntimeException("启动类缺少@RpcScanner 注解");
        }
        String annotationValue = mainClass.getAnnotation(RpcScanner.class).value();
        //如果注解路径的值是空，则等于main父路径包下
        if ("".equals(annotationValue)) {
            annotationValue = mainClassPath.substring(0, mainClassPath.lastIndexOf("."));
        }
        //获取所有类的set集合
        Set<Class<?>> set = PackageScanUtils.getClasses(annotationValue);
        for (Class<?> c : set) {
            //@RpcService注解的类才能注册
            if (c.isAnnotationPresent(RpcService.class)) {
                RpcService service = c.getAnnotation(RpcService.class);
                //获取注解属性中的接口名
                String interfaceName = service.interfaceType().getSimpleName();
                //注册中心的服务名：接口名+版本号
                String ServerNameValue = interfaceName + "-" + service.version();
                Object object;
                try {
                    //创建实例
                    object = c.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    log.error("{}",e);
                    log.error("创建对象{}发生错误",c);
                    continue;
                }
                //注解的值如果为空，使用类名
                if ("".equals(ServerNameValue)) {
                    addServer(object, c.getCanonicalName());
                } else {
                    addServer(object, ServerNameValue);
                }
            }
        }
    }

    private void addServer(Object object, String serverName) {
        //添加到本地
        LocalServerCache.add(serverName, object);
        //注册到Nacos
        register.serverRegister(serverName, new InetSocketAddress(ip, port));
    }
}
