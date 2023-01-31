package com.parody.rpc.balancer;

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
