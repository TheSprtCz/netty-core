package org.sprt.netty.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.sprt.netty.client.ServerUser;
import org.sprt.netty.listenerObject.ClientConnectListenerObject;
import org.sprt.netty.listenerObject.ClientDisconnectListenerObject;
import org.sprt.netty.listenerObject.ServerReceiveListenerObject;
import org.sprt.netty.packets.BasicPacket;
import org.sprt.netty.packets.ObjectPacket;
import org.sprt.netty.runnable.ServerWatcher;
import org.sprt.netty.util.Attributes;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

@Sharable
public class ServerHandler extends SimpleChannelInboundHandler<BasicPacket> {

    private ServerWatcher watcher;

    public ServerHandler(ServerWatcher watcher) {
        this.watcher = watcher;
    }

    private Set<ServerReceiveListenerObject> getReceiveListeners(String header) {
        return watcher.getReceiveListeners().entrySet()
                .parallelStream()
                .filter(entry -> entry.getKey().matcher(header).matches())
                .map(Map.Entry::getValue)
                .collect(Collectors.toSet());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BasicPacket packet) throws Exception {
        // Triggers correct ServerReceiveListeners
        Set<ServerReceiveListenerObject> listeners = getReceiveListeners(packet.getHeader());
        if (listeners.size() > 0) {
            ServerUser user = new ServerUser(ctx.channel());
            for (ServerReceiveListenerObject listener : listeners) {
                if (listener.isCorrectClass(packet)) {
                    listener.invoke(user, packet);
                } else if (packet instanceof ObjectPacket) {
                    Object obj = ((ObjectPacket) packet).getObject();
                    if (listener.isCorrectClass(obj)) {
                        listener.invoke(user, obj);
                    }
                }
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        String name = channel.attr(Attributes.NAME).get();
        boolean nameExists = watcher.getUsers().parallelStream()
                .anyMatch(comparing -> comparing.attr(Attributes.NAME).get().equals(name));
                
        ServerUser user = new ServerUser(channel);
        if (nameExists) {
            user.kick("User with same name is connected");
            return;
        }

        watcher.addUser(channel);
        //System.out.println("Client connected");
 
        // Call all ClientConnectListeners
        for (ClientConnectListenerObject listener : watcher.getCntListeners()) {
            try {
                listener.invoke(user);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        //System.out.println("Client disconnected");
        String name = ctx.channel().attr(Attributes.NAME).get();
        String reason = ctx.channel().attr(Attributes.REASON).get();
        if (reason == null)
            reason = "Unknown Reason";

        // Call all ClientDisconnectListeners
        for (ClientDisconnectListenerObject listener : watcher.getDscListeners()) {
            try {
                listener.invoke(name, reason);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.channel().attr(Attributes.REASON).set(cause.getMessage());
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE) {
                ctx.channel().attr(Attributes.REASON).set("Timeout");
                ctx.close();
            } else if (e.state() == IdleState.WRITER_IDLE) {
                ctx.writeAndFlush(true);
            }
        }
    }
}
