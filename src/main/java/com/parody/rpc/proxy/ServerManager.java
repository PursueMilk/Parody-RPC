package com.parody.rpc.proxy;

import com.parody.rpc.annotation.RpcScanner;
import com.parody.rpc.annotation.RpcService;
import com.parody.rpc.nacos.NacosServerRegister;
import com.parody.rpc.nacos.ServerRegister;
import com.parody.rpc.transport.server.LocalServerCache;
import com.parody.rpc.transport.server.NettyRpcServer;
import com.parody.rpc.transport.server.RpcServer;
import com.parody.rpc.utils.PackageScanUtils;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Set;

public class ServerManager {

    private RpcServer rpcServer;

    private String ip;

    private Integer port;

    private ServerRegister register;


    public ServerManager(String ip, Integer port) {
        this.rpcServer = new NettyRpcServer();
        if (ip == null) {
            try {
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
        //System.out.println(set.size());
        for (Class<?> c : set) {
            //只有有@RpcService注解的才注册
            if (c.isAnnotationPresent(RpcService.class)) {
                RpcService service = c.getAnnotation(RpcService.class);
                String interfaceName = service.interfaceType().getSimpleName();
                String ServerNameValue = interfaceName + "-" + service.version();
                Object object;
                try {
                    object = c.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                    System.err.println("创建对象" + c + "发生错误");
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
