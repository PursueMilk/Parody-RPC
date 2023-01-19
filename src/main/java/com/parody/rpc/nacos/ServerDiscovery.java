package com.parody.rpc.nacos;

import java.net.InetSocketAddress;

public interface ServerDiscovery {
    InetSocketAddress serverDiscovery(String serverName);
}
