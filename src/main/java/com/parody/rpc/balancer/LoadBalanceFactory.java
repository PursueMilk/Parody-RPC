package com.parody.rpc.balancer;

import com.parody.rpc.serialization.JsonSerialization;
import com.parody.rpc.serialization.ProtostuffSerializer;
import com.parody.rpc.serialization.RpcSerialization;
import com.parody.rpc.serialization.SerializationTypeEnum;

public class LoadBalanceFactory {

    public static LoadBalance getLoadBalance(String balanceName) {
        String realName = balanceName.toUpperCase();
        switch (realName) {
            case "RANDOM":
                return new RandomBalance();
            case "FULLROUND":
                return new FullRoundBalance();
            default:
                throw new IllegalArgumentException("balance type is illegal");
        }
    }

}
