package com.parody.rpc.protocol;

/**
 * @Author: changjiu.wang
 * @Date: 2021/7/24 22:58
 */
public class ProtocolConstants {

    //请求头的固定长度
    public static final int HEADER_TOTAL_LEN = 43;

    //请求头中的魔数
    public static final int MAGIC = 0xCAFEBABE;

    //协议版本号
    public static final byte VERSION = 0x1;

    //消息ID长度
    public static final int REQ_LEN = 32;

}
