package org.sprt.netty.interfaces;

import org.sprt.netty.packets.BasicPacket;
import org.sprt.netty.packets.ObjectPacket;

import io.netty.channel.ChannelFuture;

public interface Sendable {

    public ChannelFuture write(BasicPacket packet);
    public void flush();

    public default ChannelFuture write(String header, Object object) {
        return write(new ObjectPacket(header, object));
    }
    
    public default ChannelFuture writeAndFlush(BasicPacket packet) {
        ChannelFuture future = write(packet);
        flush();
        return future;
    }

    public default ChannelFuture writeAndFlush(String header, Object object) {
        return writeAndFlush(new ObjectPacket(header, object));
    }
}
