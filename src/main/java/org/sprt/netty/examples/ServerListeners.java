package org.sprt.netty.examples;

import java.io.IOException;

import org.sprt.netty.annotations.ClientConnectListener;
import org.sprt.netty.annotations.ClientDisconnectListener;
import org.sprt.netty.annotations.ReceiveListener;
import org.sprt.netty.client.ServerUser;

public class ServerListeners {

    @ClientConnectListener
    public void onClientConnect(ServerUser user) throws IOException {
        System.out.println("Whoa, user " + user.getName() + " has connected");
        user.writeAndFlush("test", 0);
        user.writeAndFlush("Xoxoxo", 0);
        user.writeAndFlush("oxo", 5);
    }

    @ClientDisconnectListener
    public void onClientDisconnect(String name, String reason) {
        System.out.println("User " + name + " has disconnected, reason: " + reason);
    }

    @ReceiveListener("msg")
    public void onMsg(ServerUser user, Integer msg) throws IOException {
        System.out.println(user.getName() + ":" + msg);
        if (msg.equals(99)) {
            //user.kick("You are an Asshole");
        }
    }
}
