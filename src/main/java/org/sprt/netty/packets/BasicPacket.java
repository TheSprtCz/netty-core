package org.sprt.netty.packets;

import java.io.Serializable;

public class BasicPacket implements Serializable {

    private static final long serialVersionUID = -3309912567174602546L;
    private String header;

    public BasicPacket(String header) {
        this.header = header;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }
}
