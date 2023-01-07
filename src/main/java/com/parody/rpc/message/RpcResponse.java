package com.parody.rpc.message;

import lombok.Data;

import java.io.Serializable;

@Data
public class RpcResponse<T>  implements Serializable {

    private static final long serialVersionUID = 715745410605631236L;

    /**
     * 响应状态码
     */
    private Integer code;


    /**
     * 响应信息
     */
    private String message;


    /**
     * 响应数据内容
     */
    private T data;

}
