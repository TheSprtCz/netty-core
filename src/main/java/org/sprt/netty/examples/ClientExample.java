package org.sprt.netty.examples;

import org.sprt.netty.client.NettyClient;

public class ClientExample {

    public static void main(String[] args) {
        try {
            NettyClient client = new NettyClient("localhost", 10555, "Sprt");
            client.registerListeners(new ClientListeners());
            client.connect();
            for (int i = 0; i < 2; i++) {
                client.writeAndFlush("msg2", String.valueOf(i));
            }
            //client.disconnect("Nope");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
