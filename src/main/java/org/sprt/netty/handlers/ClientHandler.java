package org.sprt.netty.handlers;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.sprt.netty.listenerObject.ConnectListenerObject;
import org.sprt.netty.listenerObject.DisconnectListenerObject;
import org.sprt.netty.packets.BasicPacket;
import org.sprt.netty.packets.ObjectPacket;
import org.sprt.netty.runnable.ClientWatcher;
import org.sprt.netty.listenerObject.ClientReceiveListenerObject;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class ClientHandler extends SimpleChannelInboundHandler<BasicPacket> {

    private ClientWatcher watcher;

    public ClientHandler(ClientWatcher watcher) {
        this.watcher = watcher;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BasicPacket packet) throws Exception {
        // Triggers correct ClientReceiveListeners
        Set<ClientReceiveListenerObject> listeners = getReceiveListeners(packet.getHeader());
        if (listeners.size() > 0) {
            for (ClientReceiveListenerObject listener : listeners) {
                if (listener.isCorrectClass(packet)) {
                    listener.invoke(packet);
                } else if (packet instanceof ObjectPacket){
                    Object obj = ((ObjectPacket) packet).getObject();
                    if (listener.isCorrectClass(obj)) {
                        listener.invoke(obj);
                    }        
                }
            }
        }
    }

    private Set<ClientReceiveListenerObject> getReceiveListeners(String header) {
        return watcher.getReceiveListeners().entrySet()
                .parallelStream()
                .filter(entry -> entry.getKey().matcher(header).matches())
                .map(Map.Entry::getValue)
                .collect(Collectors.toSet());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        for (ConnectListenerObject obj : watcher.getConnectListeners()) {
            try {
                obj.invoke();
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        for (DisconnectListenerObject obj : watcher.getDisconnectListeners()) {
            try {
                obj.invoke(watcher.getReason());
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        watcher.setReason(cause.getMessage());
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE) {
                watcher.setReason("Timeout");
                ctx.close();
            } else if (e.state() == IdleState.WRITER_IDLE) {
                ctx.writeAndFlush(true);
            }
        }
    }
}
