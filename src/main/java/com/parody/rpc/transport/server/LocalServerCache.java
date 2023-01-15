package com.parody.rpc.transport.server;

import java.util.HashMap;
import java.util.Map;

public final class LocalServerCache {
    private static final Map<String, Object> serverCacheMap = new HashMap<>();

    public static void add(String serverName, Object server) {
        if (serverCacheMap.containsKey(serverName)) {
            return;
        }
        serverCacheMap.put(serverName, server);
    }

    public static Object get(String serverName) {
        Object server = serverCacheMap.get(serverName);
        if (server == null) {
            throw new RuntimeException("未发现该服务");
        }
        return server;
    }
}
