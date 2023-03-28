package com.parody.rpc.service.impl;

import com.parody.rpc.annotation.RpcService;
import com.parody.rpc.service.ReserveService;

@RpcService
public class ReserveServiceImpl implements ReserveService {
    @Override
    public String reserve(String str) {
        StringBuilder builder = new StringBuilder();
        for (int i = str.length() - 1; i >= 0; i--) {
            builder.append(str.charAt(i));
        }
        return builder.toString();
    }
}
