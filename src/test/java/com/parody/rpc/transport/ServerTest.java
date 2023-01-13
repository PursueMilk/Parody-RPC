package com.parody.rpc.transport;

import com.parody.rpc.transport.server.NettyRpcServer;
import org.junit.jupiter.api.Test;

public class ServerTest {

    @Test
    public void ServerStart() {
        new NettyRpcServer().start("127.0.0.1",9000);
    }
}
