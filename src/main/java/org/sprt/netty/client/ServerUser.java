package org.sprt.netty.client;

import org.sprt.netty.interfaces.Sendable;
import org.sprt.netty.packets.BasicPacket;
import org.sprt.netty.packets.ObjectPacket;
import org.sprt.netty.util.Attributes;
import org.sprt.netty.util.Headers;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

public class ServerUser implements Sendable {
    private final Channel channel;
    private String name;

    public ServerUser(Channel channel) {
        this.channel = channel;
        this.name = channel.attr(Attributes.NAME).get();
    }

    public Channel getChannel() {
        return channel;
    }

    public String getName() {
        return name;
    }

    public void kick(String reason) {
        setReason(reason);
        channel.writeAndFlush(new ObjectPacket(Headers.DISCONNECTMSG, reason))
            .addListener((future) -> {
                channel.close();
            });
    }

    public void setReason(String reason) {
        channel.attr(Attributes.REASON).set(reason);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if ((object == null) || (getClass() != object.getClass())) {
            return false;
        }

        ServerUser user = (ServerUser) object;
        return channel.id().equals(user.getChannel().id());
    }

    @Override
    public ChannelFuture write(BasicPacket packet) {
        return channel.write(packet);
    }

    @Override
    public void flush() {
        channel.flush();        
    }
}
