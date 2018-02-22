package org.sprt.netty.examples;

import org.sprt.netty.client.NettyServer;

public class ServerExample {

    public static void main(String[] args) {
        try {
            NettyServer server = new NettyServer("localhost", 10555);
            server.registerListeners(new ServerListeners());
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
