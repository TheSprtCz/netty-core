package org.sprt.netty.initializers;

import org.sprt.netty.handlers.ClientHandler;
import org.sprt.netty.packets.BasicPacket;
import org.sprt.netty.packets.InitialPacket;
import org.sprt.netty.packets.ObjectPacket;
import org.sprt.netty.runnable.ClientWatcher;
import org.sprt.netty.util.Headers;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;

public class InitialClientHandler extends SimpleChannelInboundHandler<BasicPacket> {

    private final InitialPacket packet;
    private final ClientWatcher watcher;
    private ChannelPromise promise;
    
    public InitialClientHandler(InitialPacket packet, ClientWatcher watcher) {
        this.packet = packet;
        this.watcher = watcher;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BasicPacket msg) throws Exception {
        //System.out.println("Jéj zpráva");
        String header = msg.getHeader();
        if (header.equals(Headers.CNTACK)) {
            //System.out.println("Received ACK");
            // Remove itself from the eventLoop
            ctx.pipeline().remove(this);

            // Add handler for active user
            ClientHandler handler = new ClientHandler(watcher);
            ctx.pipeline().addLast(handler);
            promise.setSuccess();
            // Call connect event
            handler.channelActive(ctx);
        }
        else if (header.equals(Headers.DISCONNECTMSG) && msg instanceof ObjectPacket) {
            ctx.close();
            promise.setFailure(new IllegalStateException("Unable to connect due to: " + (String) ((ObjectPacket) msg).getObject()));
        }
        
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        promise = ctx.channel().newPromise();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(packet);
    }

    public ChannelFuture getFuture() {
        return promise;
    }
}
