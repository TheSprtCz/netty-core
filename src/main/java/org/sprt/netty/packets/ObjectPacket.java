package org.sprt.netty.packets;

public class ObjectPacket extends BasicPacket {

    private static final long serialVersionUID = -7866489865853201749L;
    private Object object;

    public ObjectPacket(String header, Object object) {
        super(header);
        this.setObject(object);
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
