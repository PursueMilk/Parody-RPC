package com.parody.rpc.balancer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 负载均衡工厂
 */
public class LoadBalanceFactory {

    private static final Map<String, LoadBalance> map = new ConcurrentHashMap<>();

    static {
        map.put("RANDOM", new RandomBalance());
        map.put("FULLROUND", new FullRoundBalance());
    }

    public static LoadBalance getLoadBalance(String balanceName) {
        String realName = balanceName.toUpperCase();
        LoadBalance loadBalance = map.get(realName);
        if (loadBalance == null) {
            throw new IllegalArgumentException("balance type is illegal");
        }
        return loadBalance;
    }
}
