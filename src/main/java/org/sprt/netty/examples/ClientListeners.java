package org.sprt.netty.examples;

import org.sprt.netty.annotations.ConnectListener;
import org.sprt.netty.annotations.DisconnectListener;
import org.sprt.netty.annotations.ReceiveListener;
import org.sprt.netty.packets.ObjectPacket;

public class ClientListeners {

    @ConnectListener
    public void onConnect() {
        System.out.println("Successfully connected to server");
    }

    @DisconnectListener
    public void onDisconnect(String reason) {
        System.out.println("Disconnected, reason: " + reason);
    }

    @ReceiveListener(".*")
    public void testRegex(ObjectPacket packet) {
        System.out.println(packet.getHeader());
    }
}
