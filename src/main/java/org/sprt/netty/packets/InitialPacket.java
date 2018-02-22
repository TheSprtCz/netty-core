package org.sprt.netty.packets;

import org.sprt.netty.util.Headers;

public class InitialPacket extends BasicPacket {

    private static final long serialVersionUID = -2387349228611887328L;
    private String name;

    public InitialPacket(String name) {
        super(Headers.INITIAL);
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
