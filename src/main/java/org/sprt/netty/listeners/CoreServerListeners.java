package org.sprt.netty.listeners;

import org.sprt.netty.annotations.PreClientConnectListener;
import org.sprt.netty.annotations.ReceiveListener;
import org.sprt.netty.client.NettyServer;
import org.sprt.netty.client.ServerUser;
import org.sprt.netty.packets.InitialPacket;
import org.sprt.netty.util.Headers;

public class CoreServerListeners {

    private NettyServer server;
    public CoreServerListeners(NettyServer nettyServer) {
        this.server = nettyServer;
    }

    @ReceiveListener(Headers.DISCONNECTMSG)
    public void receiveReason(ServerUser user, String reason) {
        user.setReason(reason);
    }
    
    @PreClientConnectListener
    public void checkName(ServerUser socket, InitialPacket packet) {
        String name = packet.getName();
        if (server.getFilteredUsers(user -> user.getName().equals(name)).size() > 0) {
            socket.kick("User with same name is already connected");
        };
    }

}
