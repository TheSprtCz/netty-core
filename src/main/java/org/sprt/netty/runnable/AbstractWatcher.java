package org.sprt.netty.runnable;

import org.sprt.netty.packets.BasicPacket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

public abstract class AbstractWatcher implements Runnable {

    protected final int port;
    protected final String address;
    protected Channel channel;
    private ChannelFuture lastMsg;

    protected AbstractWatcher(String address, int port) {
        this.address = address;
        this.port = port;
    }

    protected abstract void shutdown();

    @Override
    public void run() {
        try {
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            shutdown();
        }
    }

    public ChannelFuture write(BasicPacket packet) {
        lastMsg = channel.write(packet);
        return lastMsg;
    }

    public void flush() {
        channel.flush();
    }

    public ChannelFuture writeAndFlush(BasicPacket packet) {
        lastMsg = channel.writeAndFlush(packet);
        return lastMsg;
    }

    public void close() throws InterruptedException {
        if (lastMsg != null) {
            lastMsg.sync();
        }
        channel.close();
    }

    public Channel getChannel() {
        return channel;
    }

}
