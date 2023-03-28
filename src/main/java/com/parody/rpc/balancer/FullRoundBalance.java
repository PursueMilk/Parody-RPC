package com.parody.rpc.balancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询
 */
public class FullRoundBalance implements LoadBalance{


    private AtomicInteger atomicInteger = new AtomicInteger(0);

    /**
     * 防止Integer越界 超过Integer最大值
     */
    private final int getAndIncrement() {
        int current;
        int next;
        do {
            current = this.atomicInteger.get();
            next = current >= 2147483647 ? 0 : current + 1;
        } while (!this.atomicInteger.compareAndSet(current, next));
        return next;
    }


    @Override
    public Instance getInstance(List<Instance> list) {
        int index = getAndIncrement() % list.size();
        return list.get(index);
    }
}
