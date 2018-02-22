package org.sprt.netty.client;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.sprt.netty.annotations.processors.ServerAnnotationProcessor;
import org.sprt.netty.listeners.CoreServerListeners;
import org.sprt.netty.packets.InitialPacket;
import org.sprt.netty.runnable.ServerWatcher;

public class NettyServer {

    private final int port;
    private final String address;
    private final Class<? extends InitialPacket> packetClass;
    private ServerWatcher watcher;
    private ServerAnnotationProcessor checker;

    public NettyServer(String address, int port, Class<? extends InitialPacket> packetClass) {
        this.port = port;
        this.address = address;
        this.packetClass = packetClass;
        watcher = new ServerWatcher(address, port, packetClass);
        checker = new ServerAnnotationProcessor(watcher);
        registerListeners(new CoreServerListeners(this));
    }

    public NettyServer(String address, int port) {
        this(address, port, InitialPacket.class);
    }

    public void start() throws Exception {
        watcher.start();
        Thread watchThread = new Thread(watcher);
        watchThread.setName("NettyWatcherThread");
        watchThread.start();
    }

    public void close() {
        watcher.close();
    }

    public void close(String reason) {
        watcher.close(reason);
    }

    public void registerListeners(Object obj) {
        checker.processObject(obj);
    }

    public int getPort() {
        return port;
    }

    public String getAddress() {
        return address;
    }

    public Class<? extends InitialPacket> getPacketClass() {
        return packetClass;
    }

    public ServerAnnotationProcessor getChecker() {
        return checker;
    }

    public void setChecker(ServerAnnotationProcessor checker) {
        this.checker = checker;
    }
   
    public List<ServerUser> getUsers() {
        return watcher.getUsers().parallelStream()
            .map(channel -> new ServerUser(channel)).collect(Collectors.toList());
    }

    public List<ServerUser> getFilteredUsers(Predicate<? super ServerUser> predicate) {
        return watcher.getUsers().parallelStream()
            .map(channel -> new ServerUser(channel))
            .filter(predicate)
            .collect(Collectors.toList());
    }

}
