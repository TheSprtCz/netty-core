package org.sprt.netty.runnable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

import org.sprt.netty.initializers.InitialClientHandler;
import org.sprt.netty.listenerObject.ClientReceiveListenerObject;
import org.sprt.netty.listenerObject.ConnectListenerObject;
import org.sprt.netty.listenerObject.DisconnectListenerObject;
import org.sprt.netty.packets.InitialPacket;
import org.sprt.netty.packets.ObjectPacket;
import org.sprt.netty.util.Headers;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.timeout.IdleStateHandler;

public class ClientWatcher extends AbstractWatcher {

    private EventLoopGroup group;
    private String reason = "Server closed";
    
    private InitialPacket packet;
    private CopyOnWriteArrayList<DisconnectListenerObject> dscListeners = new CopyOnWriteArrayList<DisconnectListenerObject>();
    private CopyOnWriteArrayList<ConnectListenerObject> cntListeners = new CopyOnWriteArrayList<ConnectListenerObject>();
    private ConcurrentHashMap<Pattern, ClientReceiveListenerObject> receiveListeners = new ConcurrentHashMap<Pattern, ClientReceiveListenerObject>();    
    
    public ClientWatcher(String address, int port) {
        super(address, port);
    }

    public void shutdown() {
        group.shutdownGracefully();
    }

    public void connect() throws InterruptedException {
        group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        final ClientWatcher watcher = this;
        InitialClientHandler handler = new InitialClientHandler(packet, watcher);
        bootstrap.group(group)
         .channel(NioSocketChannel.class)
         .handler(new ChannelInitializer<SocketChannel>() {
             @Override
             public void initChannel(SocketChannel channel) throws Exception {
                 ChannelPipeline pipeline = channel.pipeline();

                 pipeline.addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                 pipeline.addLast(new ObjectEncoder());
                 //pipeline.addLast(new LoggingHandler(LogLevel.INFO));
                 pipeline.addLast(new IdleStateHandler(20, 10, 0));
                 pipeline.addLast(handler);
             }
         });

        // Start the client.
        ChannelFuture future = bootstrap.connect(address, port).sync();
        channel = future.channel();

        // Wait for handler to finish initialization
        handler.getFuture().sync().await(5000);
    }

    public CopyOnWriteArrayList<DisconnectListenerObject> getDisconnectListeners() {
        return dscListeners;
    }

    public CopyOnWriteArrayList<ConnectListenerObject> getConnectListeners() {
        return cntListeners;
    }

    public ConcurrentHashMap<Pattern, ClientReceiveListenerObject> getReceiveListeners() {
        return receiveListeners;
    }

    public void addConnectListener(ConnectListenerObject listener) {
        cntListeners.add(listener);
    }

    public void addDisconnectListener(DisconnectListenerObject listener) {
        dscListeners.add(listener);
    }

    public void addReceiveListener(String header, ClientReceiveListenerObject listener) {
        receiveListeners.put(Pattern.compile(header), listener);
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public InitialPacket getPacket() {
        return packet;
    }

    public void setPacket(InitialPacket packet) {
        this.packet = packet;
    }

    public void close(String reason) {
        this.reason = reason;
        channel.writeAndFlush(new ObjectPacket(Headers.DISCONNECTMSG, reason))
            .addListener((future) -> {
                shutdown();
            });
    }

    public void close() {
        close("Client Closing");
    }

}
