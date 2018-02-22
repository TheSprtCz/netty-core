package org.sprt.netty.annotations.processors;

import java.lang.reflect.Method;

import org.sprt.netty.annotations.PreClientConnectListener;
import org.sprt.netty.annotations.ClientConnectListener;
import org.sprt.netty.annotations.ClientDisconnectListener;
import org.sprt.netty.annotations.ReceiveListener;
import org.sprt.netty.listenerObject.ClientConnectListenerObject;
import org.sprt.netty.listenerObject.ClientDisconnectListenerObject;
import org.sprt.netty.listenerObject.PreClientConnectListenerObject;
import org.sprt.netty.listenerObject.ServerReceiveListenerObject;
import org.sprt.netty.runnable.ServerWatcher;

public class ServerAnnotationProcessor extends AbstractAnnotationProcessor {

    private ServerWatcher watcher;

    public ServerAnnotationProcessor(ServerWatcher server) {
        this.watcher = server;
    }

    @Override
    protected void process(Method method, Object obj) {
        if (method.isAnnotationPresent(ReceiveListener.class)) {
            ReceiveListener annotation = method.getAnnotation(ReceiveListener.class);
            ServerReceiveListenerObject listener = new ServerReceiveListenerObject(method, obj);
            if (!listener.isValid())
                throw new IllegalArgumentException("Method " + method.getName() + " is not correct PacketReceiveListener");

            listener.process();
            watcher.addReceiveListener(annotation.value(), listener);
        }

        if (method.isAnnotationPresent(PreClientConnectListener.class)) {
            PreClientConnectListenerObject check = new PreClientConnectListenerObject(method, obj, watcher.getPacketClass());
            if (!check.isValid())
                throw new IllegalArgumentException("Method " + method.getName() + " is not correct ClientConnectListener");

            watcher.addPreClientConnectCheck(check);
        }

        if (method.isAnnotationPresent(ClientConnectListener.class)) {
            ClientConnectListenerObject cnt = new ClientConnectListenerObject(method, obj);
            if (!cnt.isValid())
                throw new IllegalArgumentException("Method " + method.getName() + " is not correct ClientConnectListener");

            watcher.addClientConnectListener(cnt);
        }

        if (method.isAnnotationPresent(ClientDisconnectListener.class)) {
            ClientDisconnectListenerObject dsc = new ClientDisconnectListenerObject(method, obj);
            if (!dsc.isValid())
                throw new IllegalArgumentException("Method " + method.getName() + " is not correct ClientDisconnectListener");

            watcher.addClientDisconnectListener(dsc);
        }
    }

}
