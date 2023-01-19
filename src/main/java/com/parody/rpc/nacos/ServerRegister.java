package com.parody.rpc.nacos;

import java.net.InetSocketAddress;

public interface ServerRegister {
    void serverRegister(String serverName, InetSocketAddress address);
}
