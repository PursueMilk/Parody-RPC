# Parody-RPC
Parody-RPC是以Nacos作为注册中心，基于Netty实现的简易RPC框架，实现了多种序列化与负载均衡算法。

## 架构

![架构图](https://cdn.jsdelivr.net/gh/PursueMilk/img@master/img/202301302211296.png)

服务端（服务提供者）在注册中心（Nacos）中注册，消费者在注册中心找到对应的服务端通过Netty的NIO来调用服务端。

## 流程图

![image-20230131194858658](https://cdn.jsdelivr.net/gh/PursueMilk/img@master/img/202301311949880.png)

## 传输协议

```
+----------------------------------------------------------------+
| 魔数 4bytes | 协议版本 1byte | 序列化方式 1byte | 消息类型 1byte  |
+----------------------------------------------------------------+
|          消息 ID 32bytes      |          数据长度 4bytes        |
+----------------------------------------------------------------+
|                        数据内容（长度不定）                      |
+----------------------------------------------------------------+
```

| 字段       | 解释                             |
| :--------- | :------------------------------- |
| 魔数       | 魔数，标识一个协议包，0xCAFEBABE |
| 协议版本   | 该包的协议版本                   |
| 序列化方式 | 标明这个包的数据使用的序列化方式 |
| 消息类型   | 包的类型，为请求、响应、心跳包   |
| 消息ID     | 该消息的唯一标识                 |
| 数据长度   | 数据内容的字节长度               |

## 项目的可优化点

* 将传输协议升级为可扩展协议
* 增加对SPI机制的应用

* 集成 Spring 通过注解注册服务
* 集成 Spring 通过注解进行服务消费 

## 启动

在此之前请确保 Nacos 运行在本地 8848 端口，首先启动服务提供者（RpcServer），再启动消费者（RpcClient），在消费者端会输出：Hello，World

## LICENSE

Parody-RPC is under the MIT license. See the [LICENSE](https://github.com/PursueMilk/Parody-RPC/blob/master/license) file for details.
