package com.parody.rpc.message;

import lombok.Data;

@Data
public class HeartBeatMessage {
    private final String message = "HEART";
}
