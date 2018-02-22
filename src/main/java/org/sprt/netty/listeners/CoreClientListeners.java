package org.sprt.netty.listeners;

import org.sprt.netty.annotations.ReceiveListener;
import org.sprt.netty.runnable.ClientWatcher;
import org.sprt.netty.util.Headers;

public class CoreClientListeners {

    private ClientWatcher client;

    public CoreClientListeners(ClientWatcher client) {
        this.client = client;
    }

    @ReceiveListener(Headers.DISCONNECTMSG)
    public void receiveReason(String reason) {
        client.setReason(reason);
        System.out.println("Received dscReason");
    }
}
