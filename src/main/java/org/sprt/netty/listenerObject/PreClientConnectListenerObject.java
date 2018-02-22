package org.sprt.netty.listenerObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.sprt.netty.client.ServerUser;
import org.sprt.netty.packets.InitialPacket;

public class PreClientConnectListenerObject extends AbstractListener {

    private final Class<? extends InitialPacket> expectedClass;

    public PreClientConnectListenerObject(Method method, Object object, Class<? extends InitialPacket> expectedClass) {
        super(method, object);
        this.expectedClass = expectedClass;
    }

    @Override
    public boolean isValid() {
        return check(void.class,ServerUser.class, expectedClass);
    }

    public void invoke(ServerUser socket, InitialPacket packet) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        method.invoke(object, socket, packet);
    }
    
}
