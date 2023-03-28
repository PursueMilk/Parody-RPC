package com.parody.rpc.transport.server;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于缓存注册的服务对象
 */
public final class LocalServerCache {
    private static final Map<String, Object> serverCacheMap = new HashMap<>();

    /**
     * 向 Map 中添加服务实例
     */
    public static void add(String serverName, Object server) {
        if (serverCacheMap.containsKey(serverName)) {
            return;
        }
        serverCacheMap.put(serverName, server);
    }

    /**
     * 从 Map 中获取实例
     */
    public static Object get(String serverName) {
        Object server = serverCacheMap.get(serverName);
        return server;
    }
}
