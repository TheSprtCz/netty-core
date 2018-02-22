package org.sprt.netty.client;

import org.sprt.netty.annotations.processors.ClientAnnotationProcessor;
import org.sprt.netty.interfaces.Sendable;
import org.sprt.netty.listeners.CoreClientListeners;
import org.sprt.netty.packets.BasicPacket;
import org.sprt.netty.packets.InitialPacket;
import org.sprt.netty.packets.ObjectPacket;
import org.sprt.netty.runnable.ClientWatcher;

import io.netty.channel.ChannelFuture;

public class NettyClient implements Sendable {

    private final int port;
    private final String address;
    private final String name;
    private ClientWatcher watcher;
    private ClientAnnotationProcessor checker;

    public NettyClient(String address, int port, String name) {
        this.port = port;
        this.address = address;
        this.name = name;
        watcher = new ClientWatcher(address, port);
        checker = new ClientAnnotationProcessor(watcher);
        registerListeners(new CoreClientListeners(watcher));
    }

    public void connect() throws InterruptedException {
        connect(new InitialPacket(name));
    }

    public void connect(InitialPacket packet) throws InterruptedException {
        watcher.setPacket(packet);
        watcher.connect();
        if (watcher.getChannel().isActive()) {
            Thread watchThread = new Thread(watcher);
            watchThread.setName("NettyWatcherThread");
            watchThread.start();
        } else {
            System.out.println("Unable to connect");
        }
    }

    public ChannelFuture write(String header, Object text) {
        return watcher.write(new ObjectPacket(header, text));
    }

    public void flush() {
        watcher.flush();
    }

    public int getPort() {
        return port;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public void registerListeners(Object obj) {
        checker.processObject(obj);
    }

    public ClientAnnotationProcessor getChecker() {
        return checker;
    }

    public void setChecker(ClientAnnotationProcessor checker) {
        this.checker = checker;
    }

    public void disconnect(String reason) {
        watcher.close(reason);
    }

    public void disconnect() {
        watcher.close();
    }

    @Override
    public ChannelFuture write(BasicPacket packet) {
        return watcher.write(packet);
    }
}
