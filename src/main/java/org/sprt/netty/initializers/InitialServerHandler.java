package org.sprt.netty.initializers;

import java.lang.reflect.InvocationTargetException;

import org.sprt.netty.client.ServerUser;
import org.sprt.netty.handlers.ServerHandler;
import org.sprt.netty.listenerObject.PreClientConnectListenerObject;
import org.sprt.netty.packets.BasicPacket;
import org.sprt.netty.packets.InitialPacket;
import org.sprt.netty.runnable.ServerWatcher;
import org.sprt.netty.util.Attributes;
import org.sprt.netty.util.Headers;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@Sharable
public class InitialServerHandler extends SimpleChannelInboundHandler<InitialPacket> {

    private final Class<? extends InitialPacket> packetClass;
    private final ServerWatcher watcher;

    public InitialServerHandler(Class<? extends InitialPacket> packetClass, ServerWatcher watcher) {
        this.packetClass = packetClass;
        this.watcher = watcher;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, InitialPacket packet) throws Exception {
        if (packetClass.isInstance(packet)) {
            //System.out.println("Received Initial");
            Channel channel = ctx.channel();

            // Process it only if it hasn't got already name, meaning it was not proccesed
            if (!channel.hasAttr(Attributes.NAME)) {
                channel.attr(Attributes.NAME).set(packet.getName());

                callPreConnectListeners(new ServerUser(channel), packet);

                // If channel passed all tests, e.g wasn't closed along the way
                if (channel.isActive()) {
                    ctx.writeAndFlush(new BasicPacket(Headers.CNTACK));

                    // Remove itself from the eventLoop
                    ctx.pipeline().remove(this);

                    // Add handler for active user
                    ServerHandler handler = new ServerHandler(watcher);
                    ctx.pipeline().addLast(handler);

                    // Call connect event
                    handler.channelActive(ctx);
                }
            }
        }
    }

    private void callPreConnectListeners(ServerUser user, InitialPacket msg) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        for (PreClientConnectListenerObject listener : watcher.getPreConnectChecks()) {
            listener.invoke(user, msg);
        }
    }

}
