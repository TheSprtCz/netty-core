package org.sprt.netty.annotations.processors;

import java.lang.reflect.Method;

import org.sprt.netty.annotations.ConnectListener;
import org.sprt.netty.annotations.DisconnectListener;
import org.sprt.netty.annotations.ReceiveListener;
import org.sprt.netty.listenerObject.ClientReceiveListenerObject;
import org.sprt.netty.listenerObject.ConnectListenerObject;
import org.sprt.netty.listenerObject.DisconnectListenerObject;
import org.sprt.netty.runnable.ClientWatcher;

public class ClientAnnotationProcessor extends AbstractAnnotationProcessor {

    
    private ClientWatcher watcher;

    public ClientAnnotationProcessor(ClientWatcher client) {
        this.watcher = client;
    }

    @Override
    protected void process(Method method, Object obj) {
        if (method.isAnnotationPresent(ReceiveListener.class)) {
            ReceiveListener annotation = method.getAnnotation(ReceiveListener.class);
            ClientReceiveListenerObject listener = new ClientReceiveListenerObject(method, obj);
            if (!listener.isValid())
                throw new IllegalArgumentException("Method " + method.getName() + " is not correct PacketReceiveListener");

            listener.process();
            watcher.addReceiveListener(annotation.value(), listener);
        }

        if (method.isAnnotationPresent(ConnectListener.class)) {
            ConnectListenerObject cnt = new ConnectListenerObject(method, obj);
            if (!cnt.isValid())
                throw new IllegalArgumentException("Method " + method.getName() + " is not correct ConnectListener");

            watcher.addConnectListener(cnt);
        }

        if (method.isAnnotationPresent(DisconnectListener.class)) {
            DisconnectListenerObject dsc = new DisconnectListenerObject(method, obj);
            if (!dsc.isValid())
                throw new IllegalArgumentException("Method " + method.getName() + " is not correct DisconnectListener");

            watcher.addDisconnectListener(dsc);
        }
    }

}
