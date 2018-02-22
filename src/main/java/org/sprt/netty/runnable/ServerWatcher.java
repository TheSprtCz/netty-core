package org.sprt.netty.runnable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

import org.sprt.netty.initializers.InitialServerHandler;
import org.sprt.netty.listenerObject.PreClientConnectListenerObject;
import org.sprt.netty.listenerObject.ClientConnectListenerObject;
import org.sprt.netty.listenerObject.ClientDisconnectListenerObject;
import org.sprt.netty.listenerObject.ServerReceiveListenerObject;
import org.sprt.netty.packets.InitialPacket;
import org.sprt.netty.packets.ObjectPacket;
import org.sprt.netty.util.Headers;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.GlobalEventExecutor;

public class ServerWatcher extends AbstractWatcher {

    private EventLoopGroup workerGroup;
    private EventLoopGroup bossGroup;

    private final Class<? extends InitialPacket> packetClass;
    private ChannelGroup users = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    // Listeners
    private CopyOnWriteArrayList<PreClientConnectListenerObject> preConnectChecks = new CopyOnWriteArrayList<PreClientConnectListenerObject>();
    private CopyOnWriteArrayList<ClientConnectListenerObject> cntListeners = new CopyOnWriteArrayList<ClientConnectListenerObject>();
    private CopyOnWriteArrayList<ClientDisconnectListenerObject> dscListeners = new CopyOnWriteArrayList<ClientDisconnectListenerObject>();
    private ConcurrentHashMap<Pattern, ServerReceiveListenerObject> receiveListeners = new ConcurrentHashMap<Pattern, ServerReceiveListenerObject>();

    public ServerWatcher(String address, int port, Class<? extends InitialPacket> packetClass) {
        super(address, port);
        this.packetClass = packetClass;
    }

    public void shutdown() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    public void start() throws InterruptedException {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        final ServerWatcher watcher = this;
        bootstrap.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .option(ChannelOption.SO_BACKLOG, 100)
            //.handler(new LoggingHandler(LogLevel.INFO))
            .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel channel) throws Exception {
                    ChannelPipeline pipeline = channel.pipeline();
                    //pipeline.addLast(new LoggingHandler(LogLevel.INFO));
                    //pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
                    pipeline.addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                    pipeline.addLast(new ObjectEncoder());
                    pipeline.addLast(new IdleStateHandler(20, 10, 0));
                    pipeline.addLast(new InitialServerHandler(packetClass, watcher));
                }
            });

        // Start the server.
        ChannelFuture future = bootstrap.bind(address, port).sync();
        channel = future.channel();
        System.out.println("Server started");
    }

    public void addPreClientConnectCheck(PreClientConnectListenerObject check) {
        preConnectChecks.add(check);
    }

    public void addClientConnectListener(ClientConnectListenerObject listener) {
        cntListeners.add(listener);
    }

    public void addClientDisconnectListener(ClientDisconnectListenerObject listener) {
        dscListeners.add(listener);
    }

    public void addReceiveListener(String header, ServerReceiveListenerObject listener) {
        receiveListeners.put(Pattern.compile(header), listener);
    }

    public CopyOnWriteArrayList<PreClientConnectListenerObject> getPreConnectChecks() {
        return preConnectChecks;
    }

    public CopyOnWriteArrayList<ClientConnectListenerObject> getCntListeners() {
        return cntListeners;
    }

    public CopyOnWriteArrayList<ClientDisconnectListenerObject> getDscListeners() {
        return dscListeners;
    }

    public ConcurrentHashMap<Pattern, ServerReceiveListenerObject> getReceiveListeners() {
        return receiveListeners;
    }

    public Class<? extends InitialPacket> getPacketClass() {
        return packetClass;
    }

    public void close(String reason) {
        users.writeAndFlush(new ObjectPacket(Headers.DISCONNECTMSG, reason))
            .addListener((future) -> {
                shutdown();
            });
    }

    public void close() {
        close("Server Closing");
    }

    public void addUser(Channel user) {
        users.add(user);
    }

    public ChannelGroup getUsers() {
        return users;
    }
}
