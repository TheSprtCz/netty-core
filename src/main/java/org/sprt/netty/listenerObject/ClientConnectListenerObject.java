package org.sprt.netty.listenerObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.sprt.netty.client.ServerUser;

public class ClientConnectListenerObject extends AbstractListener {

    public ClientConnectListenerObject(Method method, Object object) {
        super(method, object);
    }

    @Override
    public boolean isValid() {
        return check(void.class, ServerUser.class);
    }

    public void invoke(ServerUser user) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        method.invoke(object, user);
    }
}
