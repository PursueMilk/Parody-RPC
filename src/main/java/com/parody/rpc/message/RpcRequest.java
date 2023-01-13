package com.parody.rpc.message;

import lombok.Data;

import java.io.Serializable;

@Data
public class RpcRequest implements Serializable {

    private static final long serialVersionUID = 1905122041950251206L;

    /**
     * 请求的接口
     */
    private String interfaceName;

    /**
     * 请求调用的方法名
     */
    private String methodName;

    /**
     * 参数类型
     */
    private Class<?>[] parameterTypes;


    /**
     * 参数
     */
    private Object[] parameters;

    /**
     * 版本号
     */
    private String version;




}